/**
 * Created by beepbeat/holladiewal on 25.12.2017.
 */
import Commands.Companion.commands
import jdk.nashorn.internal.objects.NativeArray.forEach
import org.apache.commons.io.IOUtils
import org.apache.commons.io.filefilter.NameFileFilter
import sx.blah.discord.handle.impl.obj.Guild
import java.io.File
import java.io.FileFilter
import java.io.FilenameFilter
import java.io.PrintWriter
import java.nio.charset.Charset
import java.util.*

public class Commands{
    companion object {
        public val commands = Commands()
    }
    val varMap = mutableMapOf<String, MutableMap<String, String>>()

}
/**
 * @return true if getCommandFile already existed, false when it has been initialized with defaults
 */
fun initCommands(file: File, guildId: String) : Boolean{
    if (!commands.varMap.containsKey(guildId)) commands.varMap.put(guildId, mutableMapOf())
    commands.varMap[guildId]!!["command"] = "THIS SHOULD NEVER BE SHOWN!"
    if (!file.exists()) {
        file.createNewFile()
        commands.varMap[guildId]!!["help"] = "%n, You can use the following commands: %{cmdlist}."
        saveCommands(file, guildId)
        return false
    }
    return true
}

fun readAllCommands(){
    File(".").listFiles(CommandFileFilter()).forEach { readCommands(it, it.nameWithoutExtension.substringAfter("commands-")) }
}

fun saveAllCommands(){
    File(".").listFiles(CommandFileFilter()).forEach { saveCommands(it, it.nameWithoutExtension.substringAfter("commands-")) }
}

fun initAllCommands(){
    File(".").listFiles(CommandFileFilter()).forEach { initCommands(it, it.nameWithoutExtension.substringAfter("commands-")) }
}

internal fun readCommands(file: File, guildId: String){
    if (!commands.varMap.containsKey(guildId)) commands.varMap.put(guildId, mutableMapOf())
    commands.varMap[guildId]!!.clear()

    val lines : MutableList<String> = IOUtils.readLines(file.inputStream(), Charset.defaultCharset())
    lines
            .filter { it -> it.reduce().toCharArray()[0] != '#' } //allows for comments
            .map { it -> it.reduce()}
            .forEach { msg ->
                val key = msg.split(": ", limit=2)[0]
                commands.varMap[guildId]!!.put(key, msg.reduce().split("$key: ")[1].toLowerCase())
            }

}

fun getCommand(guildId: String, commandString: String): String {
    return if (commands.varMap.containsKey(guildId) && commands.varMap[guildId]!!.containsKey(commandString)) {Commands.commands.varMap[guildId]!![commandString]!!} else "Command doesnt exist"
}

internal fun saveCommands(file: File, guildId: String){
    PrintWriter(file.path).close()
    val tmp = ArrayList<String>()
    val stream = file.outputStream()

    commands.varMap[guildId]!!.keys.forEach {
        val value = commands.varMap[guildId]!![it]
        tmp.add("$it: $value")
    }


    IOUtils.writeLines(tmp, null, stream, Charset.defaultCharset())
    stream.flush()
    stream.close()
}

class CommandFileFilter() : FileFilter{
    override fun accept(pathname: File?): Boolean {
        return (pathname!!.nameWithoutExtension.startsWith("commands-"))
    }

}