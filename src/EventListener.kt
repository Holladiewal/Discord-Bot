import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.ReadyEvent
import sx.blah.discord.handle.impl.events.guild.channel.message.MentionEvent
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.impl.events.guild.member.NicknameChangedEvent
import sx.blah.discord.handle.obj.IMessage
import java.io.File

class EventListener{
    @EventSubscriber
    fun onReadyEvent(event : ReadyEvent){
        event.client.guilds.forEach { it.commands }
    }

    @EventSubscriber
    fun onNickChange(event: NicknameChangedEvent){
        when(event.guild.nicks.nickState(event.newNickname.get(), event.user)){
            "claimed" -> {
                event.guild.setUserNickname(event.user, event.oldNickname.get())
                event.user.orCreatePMChannel.sendMessage("The nick you tried to use has been claimed. Your Nick has been reset to its old value. If you believe this is an error, please contact your server moderator")
                return
            }
            "blocked" -> {

            }
        }
    }

    @EventSubscriber
    fun onMessageRecievedEvent(event : MessageReceivedEvent){
        val commandChar = event.guild.settings.getCommandChar()
        if (!event.message.content.startsWith(commandChar) || event.author.stringID == event.client.ourUser.stringID) return
        val message = event.message
        val guild = event.guild


        try {
            //region command
            if (message.content.startsWith("${commandChar}command")){
                val wordList = message.content.split(" ",  limit=4)
                if (wordList.size <= 1){
                    //event.respond("please use this command with parametes. Further help is not available")
                    throw UsageError()
                }
                when (wordList[1]){
                    "add" -> {
                        if (guild.hasCommand(wordList[2])) {
                            event.respond("Command \" ${wordList[2]} \" already exists, use \"!command modify\" to modify.")
                            return
                        }
                        guild.addCommand(wordList[2], wordList[3])
                        event.respond("Command \" ${wordList[2]} \" sucessfully set.")
                        return
                    }
                    "del", "delete" -> {
                        if (!guild.hasCommand(wordList[2])) {
                            event.respond("Command \" ${wordList[2]} \" doesn't exists!")
                            return
                        }
                        guild.removeCommand(wordList[2])
                        event.respond("Command \" ${wordList[2]} \" successfully deleted!")
                        return
                    }
                    "modify" -> {
                        if (!guild.hasCommand(wordList[2])) {
                            event.respond("Command \" ${wordList[2]} \" doesn't exists!")
                            return
                        }
                        guild.addCommand(wordList[2], wordList[3])
                        event.respond("Command \" ${wordList[2]} \" successfully modified.")
                        return
                    }
                    "inspect" -> {
                        event.respond(if (guild.hasCommand(wordList[2])) guild.getCommand(wordList[2]) else "That command... it vanished.... couldn't find anything in my database... you sure it still exists or ever existed?")
                        return
                    }
                    else -> {
                 //       event.respond("YOU did something wrong! Further help is currently not available")
                        UsageError()
                        return
                    }
                }
            }
            //endregion
            //region bot
            if (message.content.startsWith("${commandChar}bot")){
                if (guild.permissions.getLevel(event.author) <= 1) {
                    event.respond("Sorry, but your security clearance does not allow you to play with these settings.")
                    return
                }
                val wordlist = message.content.split(" ", limit=6)
                if (wordlist.size <= 1){
                    throw UsageError()
                }
                when (wordlist[1]){
                    "commandchar" -> {
                        when (wordlist[2]) {
                           "get", "show" -> event.respond("The command char is: ${guild.settings.getCommandChar()}")
                           "set" -> {
                               if (wordlist.size < 4) throw UsageError()
                               guild.settings.setCommandChar(wordlist[3])
                               event.respond("The command char has been set to: ${guild.settings.getCommandChar()}")
                               event.guild.log("Command char has been set to: ${guild.settings.getCommandChar()} by ${event.author.name}#${event.author.discriminator}")
                           }
                        }
                    }
                    "qlineaction" ->{

                        when (wordlist[2]) {
                            "get", "show" -> event.respond("Current QLine action is: ${guild.settings.getqlineaction()}")
                            "set" -> {
                                if (guild.permissions.getLevel(event.author) <= 3) {
                                    event.respond("Sorry, but your security clearance does not allow you to play with these settings.")
                                    return
                                }
                                if (wordlist.size < 4) throw UsageError()
                                guild.settings.setqlineaction(wordlist[3])
                                event.respond("Qline action has been set to: ${guild.settings.getqlineaction()}")
                                event.guild.log("Qline action has been set to: ${guild.settings.getqlineaction()} by ${event.author.name}#${event.author.discriminator}")
                            }
                        }
                    }
                    "nick" ->{
                        if (guild.permissions.getLevel(event.author) <= 2) {
                            event.respond("Sorry, but your security clearance does not allow you to play with these settings.")
                            return
                        }
                        when(wordlist[2]){
                            "qline" -> {
                                if (guild.permissions.getLevel(event.author) <= 3) {
                                    event.respond("Sorry, but your security clearance does not allow you Qline or unQline a nick.")
                                    return
                                }
                                when (wordlist[3]){
                                    "add", "set" ->{
                                        guild.nicks.qlineNick(wordlist[4])
                                        guild.log("QLINE: ${message.author.name}#${message.author.discriminator} added ${wordlist[4]} to the list of QLined nicks.")
                                    }
                                    "remove" ->{
                                        guild.nicks.unQlineNick(wordlist[4])
                                        guild.log("QLINE: ${message.author.name}#${message.author.discriminator} removed ${wordlist[4]} from the list of QLined nicks.")
                                    }
                                }
                            }

                            "block" -> {
                                if (guild.permissions.getLevel(event.author) <= 2) {
                                    event.respond("Sorry, but your security clearance does not allow you block or unblock a nick.")
                                    return
                                }
                                when (wordlist[3]){
                                    "add", "set" -> {
                                        guild.nicks.blockNick(wordlist[4])
                                        guild.log("BLOCK: ${message.author.name}#${message.author.discriminator} added ${wordlist[4]} to the list of blocked nicks.")
                                    }

                                    "remove", "unblock" -> {
                                        guild.nicks.unblockNick(wordlist[4])
                                        guild.log("BLOCK: ${message.author.name}#${message.author.discriminator} removed ${wordlist[4]} from the list of blocked nicks.")
                                    }
                                }
                            }

                            "claim" -> {
                                when(wordlist[3]){
                                    "claim", "add" -> {
                                        guild.nicks.claimNick(wordlist[4], event.author)
                                        guild.log("CLAIM: ${wordlist[4]} has been claimed by ${event.author.name}#${event.author.discriminator}")
                                    }
                                    "unclaim, free" -> {
                                        if (guild.nicks.getClaimedNickOwner(wordlist[4]) == "${event.author.name}#${event.author.discriminator}" || guild.permissions.getLevel(event.author) >= 4){
                                            guild.nicks.unclaimNick(wordlist[4])
                                        }
                                    }
                                }
                            }
                        }
                    }
                    "log" -> {
                        var lineCount = 5
                        if (wordlist.size >= 3) lineCount = wordlist[2].toInt()
                        File("./guilds/${guild.stringID}/log").readLines().takeLast(lineCount).forEach { event.respond(it) }
                    }
                    "roles", "clearances" -> {
                        when(wordlist[2]){
                            "add" -> {
                                when(wordlist[3]){
                                    "1" -> {
                                        if (guild.permissions.getLevel(event.author) >= 2) {
                                            guild.permissions.addRole(wordlist[4], 1)
                                        }
                                    }
                                    "2" ->{
                                        if (guild.permissions.getLevel(event.author) >= 3) {
                                            guild.permissions.addRole(wordlist[4], 2)
                                        }
                                    }
                                    "3" -> {
                                        if (guild.permissions.getLevel(event.author) >= 4) {
                                            guild.permissions.addRole(wordlist[4], 3)
                                        }
                                    }
                                    "4" -> {
                                        if (guild.permissions.getLevel(event.author) >= 5) {
                                            guild.permissions.addRole(wordlist[4], 4)
                                        }
                                    }
                                    "5" -> {
                                        event.respond("You seriously tried giving other people the security clearance of the server owner? Good try, but no... this won't work...")
                                    }
                                    else -> {
                                        event.respond("Handing out non-existing security clearances won't allow any more access than you currently have, and it will also cause A LOT of suspicion! Pro Tip: Try better next time!")
                                    }
                                }
                            }

                            "remove" -> {
                                when(wordlist[3]) {
                                    "1" -> {
                                        if (guild.permissions.getLevel(event.author) >= 2) {
                                            guild.permissions.removeRole(wordlist[4])
                                        }
                                    }
                                    "2" -> {
                                        if (guild.permissions.getLevel(event.author) >= 3) {
                                            guild.permissions.removeRole(wordlist[4])
                                        }
                                    }
                                    "3" -> {
                                        if (guild.permissions.getLevel(event.author) >= 4) {
                                            guild.permissions.removeRole(wordlist[4])
                                        }
                                    }
                                    "4" -> {
                                        if (guild.permissions.getLevel(event.author) >= 5) {
                                            guild.permissions.removeRole(wordlist[4])
                                        }
                                    }
                                    "5" -> {
                                        event.respond("You seriously tried giving other people the security clearance of the server owner? Good try, but no... this won't work...")
                                    }
                                    else -> {
                                        event.respond("Handing out non-existing security clearances won't allow any more access than you currently have, and it will also cause A LOT of suspicion! Pro Tip: Try better next time!")
                                    }
                                }
                            }
                        }
                    }
                }
                return
            }
            //endregion
        } catch (e: UsageError) {
        }
        catch (e: ArrayIndexOutOfBoundsException){}

        val messageToBeSend = parseMessage(message, guild.getCommand(message.content.split(" ", limit=2)[0].substring(1).trim()))
        if (!message.info.containsKey("target"))
            event.respond(messageToBeSend)
        else {
            if (message.info["target"] == "pm")
                event.author.orCreatePMChannel.sendMessage(messageToBeSend) //actually "getOrCreatePMChannel", but kotlin thinks its a getter...
            else
                guild.getChannelByID(message.info["target"]!!.toLong()).sendMessage(messageToBeSend)
        }

    }

    @EventSubscriber
    fun onMentionEvent(event: MentionEvent){
        //Maybe implement
    }



    fun parseMessage(message: IMessage, input: String) : String{
        message.info.clear()
        val nick = message.author
        if (!input.contains("%")) {
            return input
        }

        var retVal = input
        var inpList = input.split(" ")
        inpList.forEach{
            if (!it.startsWith("%")) return@forEach
            //region %{cmdlist}
            var tmpString = ""
            message.guild.getAllCommands().keys.forEach{ tmpString += "$it, " }
            if (tmpString.endsWith(", ")){tmpString = tmpString.removeSuffix(", ")}
            retVal = retVal.replace("%{cmdlist}", tmpString)
            //endregion
            //region %n, %{nick}
            retVal = retVal.replace("%n", nick.getDisplayName(message.guild), true).replace("%{nick}", nick.getDisplayName(message.guild), true)
            //endregion
            //region %m, %{mention}
            retVal = retVal.replace("%m", nick.mention(), true).replace("%{mention}", nick.mention(), true)
            //endregion
            //region %{pm}
            if (retVal.contains("%{pm}")) {
                message.info["target"] = "pm"
                retVal = retVal.replace("%{pm}", "")
            }
            //endregion
            //region %{redirect}
            if (retVal.contains("%{redirect:")){
                val id = retVal.substringAfter("%{redirect:").substringBefore("}")
                message.info["target"] = id
                retVal = retVal.replace("%{redirect:$id}", "")
            }

            //endregion

        }
        return retVal
    }
}

