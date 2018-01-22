/**
 * Created by beepbeat/holladiewal on 25.12.2017.
 */
import Commands.Companion.commands
import jdk.nashorn.internal.objects.NativeArray.forEach
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.PrintWriter
import java.nio.charset.Charset
import java.util.*

val commandFile = File("commands")



public class Commands{
    companion object {
        public val commands = Commands()
    }
    val varMap = mutableMapOf<String, String>()

}
/**
 * @return true if getCommandFile already existed, false when it has been initialized with defaults
 */
fun initCommands() : Boolean{
    commands.varMap["command"] = "THIS SHOULD NEVER BE SHOWN!"
    if (!commandFile.exists()) {
        commandFile.createNewFile()
        commands.varMap["help"] = "%n, You can use the following commands: %{cmdlist}."
        saveCommands()
        return false
    }
    return true
}

fun readCommands(){
    commands.varMap.clear()
    val lines : MutableList<String> = IOUtils.readLines(commandFile.inputStream(), Charset.defaultCharset())
    lines
            .filter { it -> it.reduce().toCharArray()[0] != '#' } //allows for comments
            .map { it -> it.reduce()}
            .forEach { msg ->
                val key = msg.split(": ", limit=2)[0]
                commands.varMap.put(key, msg.reduce().split("$key: ")[1].toLowerCase())
            }

}

fun saveCommands(){
    PrintWriter(commandFile.path).close()
    val tmp = ArrayList<String>()
    val stream = commandFile.outputStream()

    commands.varMap.keys.forEach {
        val value = commands.varMap[it]
        if (it == "token"){
            tmp.add("#NEVER EVER SHARE THIS TOKEN WITH ANYBODY")
        }

        tmp.add("$it: $value")
    }


    IOUtils.writeLines(tmp, null, stream, Charset.defaultCharset())
    stream.flush()
    stream.close()
}