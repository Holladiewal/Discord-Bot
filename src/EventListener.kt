import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.ReadyEvent
import sx.blah.discord.handle.impl.events.guild.channel.message.MentionEvent
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.IMessage

class EventListener{
    val commandChar = '$'
    @EventSubscriber
    fun onReadyEvent(event : ReadyEvent){
        event.client.guilds.forEach { it.commands }
    }

    @EventSubscriber
    fun onMessageRecievedEvent(event : MessageReceivedEvent){
        if (!event.message.content.startsWith(commandChar) || event.author.stringID == event.client.ourUser.stringID) return
        val message = event.message
        val guild = event.guild

        //region command
        if (message.content.startsWith("${commandChar}command")){
            val wordList = message.content.split(" ",  limit=4)
            if (wordList.size <= 1){
                event.respond("please use this command with parametes. Further help is not available")
                return
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
                else -> {
                    event.respond("YOU did something wrong! Further help is currently not available")
                    return
                }
            }
        }
        //endregion

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

