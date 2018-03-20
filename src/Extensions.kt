import sx.blah.discord.api.internal.json.objects.EmbedObject
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.handle.impl.obj.Guild
import sx.blah.discord.handle.obj.IGuild
import sx.blah.discord.handle.obj.IMessage
import java.io.File
import kotlin.properties.Delegates

fun MessageEvent.respond(message: String, tts: Boolean = false){
    if (message.isNotBlank() && message.isNotEmpty())
        if (tts)
            this.channel.sendMessage(message, tts)
        else
            MessageQueue.addToQueue(this.guild.stringID, this.channel.stringID, message)
}

fun MessageEvent.respond(message: String, embed: EmbedObject, tts: Boolean = false){
    this.channel.sendMessage(message, embed, tts)
}

fun String.reduce() : String{
    val tmp1 = this.trim().split(" ").toMutableList()
    tmp1.removeAll { it.trim() == "" }
    val sb = StringBuilder()
    tmp1.forEach{ sb.append(it + " ") }
    return sb.toString().trim()
}

val IMessage.info: MutableMap<String, String> by Delegates.observable(mutableMapOf(), { _, _, _ -> Unit})
val IGuild.commands: CommandHandler
    get() {
        if (CommandHandler.instances.containsKey(this.stringID)) return CommandHandler.instances[this.stringID]!!
        val tmp = CommandHandler(this as Guild)
        CommandHandler.instances.put(this.stringID, tmp)
        return tmp
    }

val IGuild.settings: SettingsHandler
    get() {
        if (SettingsHandler.instances.containsKey(this.stringID)) return SettingsHandler.instances[this.stringID]!!
        val tmp = SettingsHandler(this as Guild)
        SettingsHandler.instances.put(this.stringID, tmp)
        return tmp
    }

val IGuild.permissions: PermissionsManager
    get() {
        if (PermissionsManager.instances.containsKey(this.stringID)) return PermissionsManager.instances[this.stringID]!!
        val tmp = PermissionsManager(this as Guild)
        PermissionsManager.instances.put(this.stringID, tmp)
        return tmp
    }

val IGuild.nicks: NickManager
    get() {
        if (NickManager.instances.containsKey(this.stringID)) return NickManager.instances[this.stringID]!!
        val tmp = NickManager(this as Guild)
        NickManager.instances.put(this.stringID, tmp)
        return tmp
    }

val IGuild.ranks: RankFileHandler
    get() {
        if (RankFileHandler.instances.containsKey(this.stringID)) return RankFileHandler.instances[this.stringID]!!
        val tmp = RankFileHandler(this as Guild)
        RankFileHandler.instances.put(this.stringID, tmp)
        return tmp
    }

fun IGuild.getCommand(name: String): String = this.commands.getCommand(name)
fun IGuild.hasCommand(name: String): Boolean = this.commands.hasCommand(name)
fun IGuild.addCommand(name: String, msg: String){
    this.commands.addCommand(name, msg)
}
fun IGuild.removeCommand(name: String){
    this.commands.removeCommand(name)
}
fun IGuild.getAllCommands(): Map<String, String> = commands.getAllCommands()

fun IGuild.log(text: String){
    File("./guilds/${this.stringID}/log").printWriter().use{ out ->
        out.println(text)
        out.flush()
        out.close()
    }
}
