import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.api.internal.json.objects.EmbedObject
import sx.blah.discord.handle.impl.events.ReadyEvent
import sx.blah.discord.handle.impl.events.guild.channel.message.MentionEvent
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.IUser

class EventListener{
    val commandChar = '$'
    @EventSubscriber
    fun onReadyEvent(event : ReadyEvent){

    }

    @EventSubscriber
    fun onMessageRecievedEvent(event : MessageReceivedEvent){
        val message = event.message
        if (!message.content.startsWith(commandChar)) return
        event.respond(buildMessage(message.author, message.content))
    }

    @EventSubscriber
    fun onMentionEvent(event: MentionEvent){
        //Maybe implement
    }



    fun buildMessage(nick : IUser, input : String) : String{
        if (!input.contains("%")) {
            return input
        }

        var retVal = input
        var inpList = input.split(" ")
        inpList.forEach{
            if (!it.startsWith("%")) return@forEach
            //region %{cmdlist}
            var tmpString = ""
            Commands.commands.varMap.keys.forEach{ tmpString += "$it, " }
            if (tmpString.endsWith(", ")){tmpString = tmpString.removeSuffix(", ")}
            retVal = retVal.replace("%{cmdlist}", tmpString)
            //endregion
            //region %n, %{nick}
            retVal = retVal.replace("%n", nick.name, true).replace("%{nick}", nick.name, true)
            //endregion
            //region %m, %{mention}
            retVal = retVal.replace("%m", nick.mention(), true).replace("%{mention}", nick.mention(), true)
            //endregion

        }
        return retVal
    }
}

fun MessageEvent.respond(message: String, tts: Boolean = false){
    this.channel.sendMessage(message, tts)
}

fun MessageEvent.respond(message: String, embed: EmbedObject, tts: Boolean){
    this.channel.sendMessage(message, embed, tts)
}