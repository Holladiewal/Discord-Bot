import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.ReadyEvent
import sx.blah.discord.handle.impl.events.guild.channel.message.MentionEvent
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.impl.events.guild.member.NicknameChangedEvent
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent
import sx.blah.discord.handle.obj.IMessage
import java.awt.Color
import java.io.File
import java.util.*

class EventListener {

    // TODO: check nick on server join.
    @EventSubscriber
    fun onReadyEvent(event: ReadyEvent) {
        event.client.guilds.forEach { it.commands }
    }

    @EventSubscriber
    fun onNickChange(event: NicknameChangedEvent) {
        when (event.guild.nicks.nickState(event.newNickname.orElse(event.user.name), event.user)) {
            "claimed" -> {
                event.guild.setUserNickname(event.user, event.oldNickname.orElse(event.user.name))
                event.user.orCreatePMChannel.sendMessage("The nick you tried to use has been claimed. Your nick change has been reverted. If you believe this is an error, contact the admins from your server")
                return
            }
            "blocked" -> {
                event.guild.setUserNickname(event.user, event.oldNickname.orElse(event.user.name))
                event.user.orCreatePMChannel.sendMessage("The nick you tried to use has been blocked. Your nick change has been reverted. If you believe this is an error, contact the admins from your server")
            }

            "qlined" -> {
                event.guild.setUserNickname(event.user, event.oldNickname.orElse(event.user.name))
                when (event.guild.settings.getqlineaction()) {
                    "ban" -> {
                        event.user.orCreatePMChannel.sendMessage("The nick you tried to use has been QLined on this server. As a result you have been automatically banned. If you believe this is an error, contact your server admins.")
                        event.guild.banUser(event.user)
                    }

                    "kick" -> {
                        event.user.orCreatePMChannel.sendMessage("The nick you tried to use has been QLined on this server. As a result you have been automatically kicked. your nick change has been reverted, therefore you should be able to rejoin safely. If you belive this was an error, please contact oyur server staff.")
                        event.guild.kickUser(event.user)
                    }
                }

            }

            "zlined" -> {

            }

            "free" -> {
                return
            }

            else -> {
                throw IllegalStateException("nickState returned unexpected value")
            }
        }
    }

    @EventSubscriber
    fun onJoin(event: UserJoinEvent){
        when (event.guild.nicks.nickState(event.user.getDisplayName(event.guild), event.user)) {
            "claimed" -> {
                event.guild.setUserNickname(event.user, UUID.randomUUID().toString().replace("-", ""))
                event.user.orCreatePMChannel.sendMessage("The nick you tried to use has been claimed. You have been assigned a random name. If you believe this is an error, contact the admins from your server")
                return
            }
            "blocked" -> {
                event.guild.setUserNickname(event.user, UUID.randomUUID().toString().replace("-", ""))
                event.user.orCreatePMChannel.sendMessage("The nick you tried to use has been blocked. You have been assigned a random name. If you believe this is an error, contact the admins from your server")
            }

            "qlined" -> {
                when (event.guild.settings.getqlineaction()) {
                    "ban" -> {
                        event.user.orCreatePMChannel.sendMessage("The nick you tried to use has been QLined on this server. As a result you have been automatically banned. If you believe this is an error, contact your server admins.")
                        event.guild.banUser(event.user)
                    }

                    "kick" -> {
                        event.user.orCreatePMChannel.sendMessage("The nick you tried to use has been QLined on this server. As a result you have been automatically kicked. your nick change has been reverted, therefore you should be able to rejoin safely. If you belive this was an error, please contact oyur server staff.")
                        event.guild.kickUser(event.user)
                    }

                    "assignrole" -> {
                        event.guild.setUserNickname(event.user, UUID.randomUUID().toString().replace("-", ""))
                        event.user.addRole(event.guild.getRoleByID(event.guild.settings.getTimeoutRole().toLong()))
                    }
                }

            }

            "zlined" -> {
                   // event.guild.setUserNickname(event.user, UUID.randomUUID().toString().replace("-", ""))
                    event.user.addRole(event.guild.getRoleByID(event.guild.settings.getTimeoutRole().toLong()))
            }

            "free" -> {
                return
            }

            else -> {
                throw IllegalStateException("nickState returned unexpected value")
            }
        }
    }

    @EventSubscriber
    fun onMessageRecievedEvent(event: MessageReceivedEvent) {
        val commandChar = event.guild.settings.getCommandChar()
        if (!event.message.content.startsWith(commandChar) || event.author.stringID == event.client.ourUser.stringID) return
        val message = event.message
        val guild = event.guild

        try {
            //region command
            if (message.content.startsWith("${commandChar}command")) {
                if (guild.permissions.getLevel(event.author) <= 1) {
                    event.respond("That security clearance isn't good enough for this room. May I see that card please?")
                    return
                }
                val wordList = message.content.split(" ", limit = 4)
                if (wordList.size <= 1) {
                    //event.respond("please use this command with parametes. Further help is not available")
                    throw UsageError()
                }
                when (wordList[1]) {
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
            if (message.content.startsWith("${commandChar}bot")) {
                if (guild.permissions.getLevel(event.author) <= 1) {
                    event.respond("Sorry, but your security clearance does not allow you to play with these settings.")
                    return
                }
                val wordlist = message.content.split(" ", limit = 6)
                if (wordlist.size <= 1) {
                    throw UsageError()
                }
                when (wordlist[1].toLowerCase()) {
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
                    "qlineaction" -> {

                        when (wordlist[2].toLowerCase()) {
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
                    "timeoutrole" -> {

                        when (wordlist[2].toLowerCase()) {
                            "get", "show" -> event.respond("Current TimeoutRole action is: ${guild.getRoleByID(guild.settings.getqlineaction().toLong()).name}")
                            "set" -> {
                                if (guild.permissions.getLevel(event.author) <= 3) {
                                    event.respond("Sorry, but your security clearance does not allow you to play with these settings.")
                                    return
                                }
                                if (wordlist.size < 4) throw UsageError()
                                guild.settings.setTimeoutRole(guild.getRolesByName(wordlist[3])[0].stringID)
                                event.respond("Timeout Role has been set to: ${guild.getRoleByID(guild.settings.getTimeoutRole().toLong()).name}")
                                event.guild.log("Timeout role has been set to: ${guild.getRoleByID(guild.settings.getTimeoutRole().toLong()).name} by ${event.author.name}#${event.author.discriminator}")
                            }
                        }
                    }
                    "timeoutchannel" -> {

                        when (wordlist[2].toLowerCase()) {
                            "get", "show" -> event.respond("Current TimeoutChanel is: ${guild.getVoiceChannelByID(guild.settings.getTimeoutChannel().toLong()).name}")
                            "set" -> {
                                if (guild.permissions.getLevel(event.author) <= 3) {
                                    event.respond("Sorry, but your security clearance does not allow you to play with these settings.")
                                    return
                                }
                                if (wordlist.size < 4) throw UsageError()
                                guild.settings.setTimeoutChannel(guild.getVoiceChannelsByName(wordlist[3])[0].stringID)
                                event.respond("Timeout channel has been set to: ${guild.getVoiceChannelByID(guild.settings.getTimeoutChannel().toLong()).name}")
                                event.guild.log("Timeout role has been set to: ${guild.getVoiceChannelByID(guild.settings.getTimeoutChannel().toLong()).name} by ${event.author.name}#${event.author.discriminator}")
                            }
                        }
                    }
                    "nick" -> {
                        if (guild.permissions.getLevel(event.author) <= 2) {
                            event.respond("Sorry, but your security clearance does not allow you to play with these settings.")
                            return
                        }
                        when (wordlist[2]) {
                            "qline" -> {
                                if (guild.permissions.getLevel(event.author) <= 3) {
                                    event.respond("Sorry, but your security clearance does not allow you Qline or unQline a nick.")
                                    return
                                }
                                when (wordlist[3]) {
                                    "add", "set" -> {
                                        guild.nicks.qlineNick(wordlist[4])
                                        guild.log("QLINE: ${message.author.name}#${message.author.discriminator} added ${wordlist[4]} to the list of QLined nicks.")
                                        val tmpList = guild.getUsersByName(wordlist[4], true)
                                        if (tmpList.isNotEmpty()) {
                                            tmpList.forEach {
                                                guild.setUserNickname(it, null)
                                                when (guild.settings.getqlineaction()) {
                                                    "kick" -> guild.kickUser(it, "You have been automatically kicked due to your nick being qlined by an admin.")
                                                    "ban" -> guild.banUser(it, "You have been automatically banned due to your nick being qlined by an admin.")
                                                }
                                            }
                                        }
                                        event.respond("Succesfully qlined ${wordlist[4]}")
                                    }
                                    "remove" -> {
                                        guild.nicks.unQlineNick(wordlist[4])
                                        guild.log("QLINE: ${message.author.name}#${message.author.discriminator} removed ${wordlist[4]} from the list of QLined nicks.")
                                        event.respond("Succesfully un-qlined ${wordlist[4]}")
                                    }
                                }
                            }

                            "zline" -> {
                                if (guild.permissions.getLevel(event.author) <= 3) {
                                    event.respond("Sorry, but your security clearance does not allow you Zline or unZline a nick.")
                                    return
                                }
                                when (wordlist[3]) {
                                    "add", "set" -> {
                                        guild.nicks.zlineUser(guild.getUsersByName(wordlist[4], true)[0])
                                        guild.log("ZLINE: ${message.author.name}#${message.author.discriminator} added ${wordlist[4]} to the list of ZLined nicks.")
                                        val tmpList = guild.getUsersByName(wordlist[4], true)
                                        if (tmpList.isNotEmpty()) {
                                            tmpList.forEach {
                                                guild.setUserNickname(it, null)
                                                when (guild.settings.getqlineaction()) {
                                                    //"kick" -> guild.kickUser(it, "You have been automatically kicked due to your nick being qlined by an admin.")
                                                    //"ban" -> guild.banUser(it, "You have been automatically banned due to your nick being qlined by an admin.")
                                                    "assignrole" -> {
                                                        guild.getUsersByName(wordlist[4], true)[0].addRole(message.guild.getRoleByID(guild.settings.getTimeoutRole().toLong()))
                                                        if (guild.getUsersByName(wordlist[4], true)[0].getVoiceStateForGuild(message.guild).channel != null) guild.getUsersByName(wordlist[4], true)[0].moveToVoiceChannel(guild.getVoiceChannelByID(guild.settings.getTimeoutChannel().toLong()))
                                                    }
                                                }
                                            }
                                        }
                                        event.respond("Succesfully zlined ${wordlist[4]}")
                                    }
                                    "remove" -> {
                                        guild.nicks.unZlineUser(guild.getUsersByName(wordlist[4], true)[0])
                                        guild.getUsersByName(wordlist[4])[0].removeRole(guild.getRoleByID(guild.settings.getTimeoutRole().toLong()))
                                        guild.log("ZLINE: ${message.author.name}#${message.author.discriminator} removed ${wordlist[4]} from the list of ZLined nicks.")
                                        event.respond("Succesfully un-zlined ${wordlist[4]}")
                                    }
                                }
                            }

                            "block" -> {
                                if (guild.permissions.getLevel(event.author) <= 2) {
                                    event.respond("Sorry, but your security clearance does not allow you block or unblock a nick.")
                                    return
                                }
                                when (wordlist[3]) {
                                    "add", "set" -> {
                                        guild.nicks.blockNick(wordlist[4])
                                        guild.log("BLOCK: ${message.author.name}#${message.author.discriminator} added ${wordlist[4]} to the list of blocked nicks.")
                                        val tmpList = guild.getUsersByName(wordlist[4], true)
                                        if (tmpList.isNotEmpty()) {
                                            tmpList.forEach {
                                                guild.setUserNickname(it, null)
                                            }
                                        }
                                        event.respond("Succesfully blocked ${wordlist[4]}")
                                    }
                                    "remove", "unblock" -> {
                                        guild.nicks.unblockNick(wordlist[4])
                                        guild.log("BLOCK: ${message.author.name}#${message.author.discriminator} removed ${wordlist[4]} from the list of blocked nicks.")
                                        event.respond("Succesfully unblocked ${wordlist[4]}")
                                    }
                                }
                            }

                            "claim" -> {
                                when (wordlist[3]) {
                                    "claim", "add" -> {
                                        guild.nicks.claimNick(wordlist[4], event.author)
                                        guild.log("CLAIM: ${wordlist[4]} has been claimed by ${event.author.name}#${event.author.discriminator}")
                                        event.respond("Succesfully claimed ${wordlist[4]}")
                                    }
                                    "unclaim", "free", "remove" -> {
                                        if (guild.nicks.getClaimedNickOwner(wordlist[4]) == "${event.author.name}#${event.author.discriminator}" || guild.permissions.getLevel(event.author) >= 4) {
                                            guild.nicks.unclaimNick(wordlist[4])
                                            event.respond("Succesfully unclaimed ${wordlist[4]}")
                                        }
                                    }
                                }
                            }
                        }
                    }
                    "log" -> {
                        var lineCount = 5
                        if (wordlist.size >= 3) lineCount = wordlist[2].toInt()
                        File("./guilds/${guild.stringID}/log").readLines().takeLast(lineCount).forEach { event.respond(it + "\n") }
                    }
                    "roles", "clearances" -> {
                        when (wordlist[2]) {
                            "add" -> {
                                when (wordlist[3]) {
                                    "1" -> {
                                        if (guild.permissions.getLevel(event.author) >= 2) {
                                            guild.permissions.addRole(wordlist[4], 1)
                                        }
                                        guild.log("${event.author.name}#${event.author.discriminator} added ${wordlist[4]} (${guild.getRoleByID(wordlist[4].toLong()).name}) to class 1")
                                        event.respond("Clearances updated.")
                                    }
                                    "2" -> {
                                        if (guild.permissions.getLevel(event.author) >= 3) {
                                            guild.permissions.addRole(wordlist[4], 2)
                                        }
                                        event.respond("Clearances updated.")
                                        guild.log("${event.author.name}#${event.author.discriminator} added ${wordlist[4]} (${guild.getRoleByID(wordlist[4].toLong()).name}) to class 2")
                                    }
                                    "3" -> {
                                        if (guild.permissions.getLevel(event.author) >= 4) {
                                            guild.permissions.addRole(wordlist[4], 3)
                                        }
                                        event.respond("Clearances updated.")
                                        guild.log("${event.author.name}#${event.author.discriminator} added ${wordlist[4]} (${guild.getRoleByID(wordlist[4].toLong()).name}) to class 3")
                                    }
                                    "4" -> {
                                        if (guild.permissions.getLevel(event.author) >= 5) {
                                            guild.permissions.addRole(wordlist[4], 4)
                                        }
                                        event.respond("Clearances updated.")
                                        guild.log("${event.author.name}#${event.author.discriminator} added ${wordlist[4]} (${guild.getRoleByID(wordlist[4].toLong()).name}) to class 4")
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
                                when (wordlist[3]) {
                                    "1" -> {
                                        if (guild.permissions.getLevel(event.author) >= 2) {
                                            guild.permissions.removeRole(wordlist[4], 1)
                                        }
                                        event.respond("Clearances updated.")
                                        guild.log("${event.author.name}#${event.author.discriminator} added ${wordlist[4]} (${guild.getRoleByID(wordlist[4].toLong()).name}) to class 1")
                                    }
                                    "2" -> {
                                        if (guild.permissions.getLevel(event.author) >= 3) {
                                            guild.permissions.removeRole(wordlist[4], 2)
                                        }
                                        event.respond("Clearances updated.")
                                        guild.log("${event.author.name}#${event.author.discriminator} added ${wordlist[4]} (${guild.getRoleByID(wordlist[4].toLong()).name}) to class 2")
                                    }
                                    "3" -> {
                                        if (guild.permissions.getLevel(event.author) >= 4) {
                                            guild.permissions.removeRole(wordlist[4], 3)
                                        }
                                        event.respond("Clearances updated.")
                                        guild.log("${event.author.name}#${event.author.discriminator} added ${wordlist[4]} (${guild.getRoleByID(wordlist[4].toLong()).name}) to class 3")
                                    }
                                    "4" -> {
                                        if (guild.permissions.getLevel(event.author) >= 5) {
                                            guild.permissions.removeRole(wordlist[4], 4)
                                        }
                                        event.respond("Clearances updated.")
                                        guild.log("${event.author.name}#${event.author.discriminator} added ${wordlist[4]} (${guild.getRoleByID(wordlist[4].toLong()).name}) to class 4")
                                    }
                                    "5" -> {
                                        event.respond("You seriously tried messsing with the security clearance of the server owner? Good try, but no... this won't work...")
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
        } catch (e: ArrayIndexOutOfBoundsException) {
        }

        val messageToBeSend = parseMessage(message, guild.getCommand(message.content.split(" ", limit = 2)[0].substring(1).trim()))
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
    fun onMentionEvent(event: MentionEvent) {
        //Maybe implement
    }



    fun parseMessage(message: IMessage, input: String): String {
        message.info.clear()
        return input.split(" ").map { parseKey(message, it).first }.joinToString { it }
    }

    fun parseKey(message: IMessage, key: String): Pair<String, Int> {
        return when {
            key.startsWith("%{cmdlist}") -> {
                var tmpString = ""
                message.guild.getAllCommands().keys.forEach { tmpString += "$it, " }
                if (tmpString.endsWith(", ")) {
                    tmpString = tmpString.removeSuffix(", ")
                }
                Pair(tmpString, 0)
            }
            key.startsWith("%n") || key.startsWith("%{nick}") -> {
                Pair(message.author.getDisplayName(message.guild), 0)
            }
            key.startsWith("%m") || key.startsWith("%{mention}") -> {
                Pair(message.author.mention(), 0)
            }
            key.startsWith("%{pm}") -> {
                message.info["target"] = "pm"
                Pair("", 0)
            }
            key.startsWith("%{params:") -> {
                var index = key.substringAfter("%{params:").substringBefore("}")
                index = if (index.startsWith("%")) parseKey(message, index).first else index
                val wordList = message.content.split(" ") as MutableList
                wordList.removeAt(0)
                when {
                    index == "all" -> {
                        var tmpString = ""
                        wordList.forEach { tmpString += it + " " }
                        tmpString = tmpString.trim()
                        Pair(tmpString, 0)
                    }
                    wordList.size <= index.toInt() -> {
                        Pair(wordList[index.toInt()], 0)
                    }
                    else -> Pair("You seem to have lost some arguments....", -1)
                }
            }
            key.startsWith("%{classneeded:") -> {
                var id = key.substringAfter("%{classneeded:").substringBefore("}")
                id = if (id.startsWith("%")) parseKey(message, id).first else id
                if (message.guild.permissions.getLevel(message.author) <= id.toCharArray()[0].toString().toInt()) return Pair("Your clearance is not enough to remove the lockdown on this command.", -1)
                Pair("", 0)
            }
            key.startsWith("%{redirect:") -> {
                if (key.contains("%{redirect:")) {
                    var id = key.substringAfter("%{redirect:").substringBefore("}")
                    id = if (id.startsWith("%")) parseKey(message, key).first else id
                    message.info["target"] = id
                }
                Pair("", 0)
            }
            key.startsWith("%{togglerole:") -> {
                var id = key.substringAfter("%{togglerole:").substringBefore("}")
                id = if (id.startsWith("%")) parseKey(message, key).first else id
                if (message.guild.getRolesByName(id).size > 0) {
                    if (!message.guild.ranks.isRank(id)) return Pair("Sorry, but that is not a rank, just a role..... IF you really want it, ask a human to give it to you!", -1)
                    if (message.guild.getRolesForUser(message.author).any { it.name == id }) {
                        message.author.removeRole(message.guild.getRolesByName(id)[0])
                        Pair("You left: $id", 1)
                    } else {
                        message.author.addRole(message.guild.getRolesByName(id)[0])
                        Pair("You joined: $id", 0)
                    }
                } else {
                    Pair("Sorry, but I can't find that role... Sure you got the right one?", -1)
                }
            }
            key.startsWith("%{giverole:") -> {
                var id = key.substringAfter("%{togglerole:").substringBefore("}")
                id = if (id.startsWith("%")) parseKey(message, key).first else id
                if (message.guild.getRolesByName(id).size > 0) {
                    if (!message.guild.ranks.isRank(id)) return Pair("Sorry, but that is not a rank, just a role..... IF you really want it, ask a human to give it to you!", -1)
                    if (!message.guild.getRolesForUser(message.author).any { it.name == id }) {
                        message.author.addRole(message.guild.getRolesByName(id)[0])
                        Pair("You joined: $id", 0)
                    } else Pair("You already have that rank", 1)
                } else {
                    Pair("Sorry, but I can't find that role... Sure you got the right one?", -1)
                }
            }
            key.startsWith("%{takerole:") -> {
                var id = key.substringAfter("%{togglerole:").substringBefore("}")
                id = if (id.startsWith("%")) parseKey(message, key).first else id
                if (message.guild.getRolesByName(id).size > 0) {
                    if (!message.guild.ranks.isRank(id)) return Pair("Sorry, but that is not a rank, just a role..... IF you really want to get rid of it, ask a human to take it from you!", -1)
                    if (message.guild.getRolesForUser(message.author).any { it.name == id }) {
                        message.author.removeRole(message.guild.getRolesByName(id)[0])
                        Pair("You left: $id", 0)
                    } else Pair("You currently don't have that rank", 1)
                } else {
                    Pair("Sorry, but I can't find that role... Sure you got the right one?", -1)
                }
            }
            key.startsWith("%{createrole:") -> {
                var id = key.substringAfter("%{createrole:").substringBefore("}")
                id = if (id.startsWith("%")) parseKey(message, key).first else id
                val role = message.guild.createRole()
                val rand = Random()
                role.changeName(id)
                role.changeColor(Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)))
                role.changeMentionable(true)
                message.guild.ranks.addRank(id)
                Pair("Role created!", 0)
            }
            key.startsWith("%{deleterole:") -> {
                var id = key.substringAfter("%{deleterole:").substringBefore("}")
                id = if (id.startsWith("%")) parseKey(message, key).first else id
                if (message.guild.ranks.isRank(id)) {
                    message.guild.ranks.removeRank(id)
                    message.guild.roles.removeIf { it.name == id }
                }
                Pair("Role deleted!", 0)
            }
            key.startsWith("%{outcome:") -> {
                val id = key.substringAfter("%{outcome:").substringBefore("}")
                Pair("${parseKey(message, id).second}", 0)

            }
            key.startsWith("%{suppress:") -> Pair("", 0)
            else -> {
                Pair(key, 0)
            }
        }
    }
}

