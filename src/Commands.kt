import org.apache.commons.io.IOUtils
import sx.blah.discord.handle.impl.obj.Guild
import java.io.File
import java.io.PrintWriter
import java.nio.charset.Charset
import java.util.*

/**
 * Created by beepbeat/holladiewal on 26.01.2018.
 */

class CommandHandler(guild: Guild) {

    companion object {
        val instances = mutableMapOf<String, CommandHandler>()
    }


    var commandFile: File
    private val varMap: MutableMap<String, String> = mutableMapOf()   // mutableMapOf<String, String>()

    init{
        File("./guilds/${guild.stringID}").mkdir()
        commandFile = File("./guilds/${guild.stringID}/commands")
        varMap["command"] = "THIS SHOULD NEVER BE SHOWN!"
        if (!commandFile.exists()) {
            commandFile.createNewFile()
            varMap["help"] = "%n, You can use the following commands: %{cmdlist}."
            saveCommands()
        }
        this.readCommands()
    }

    private fun readCommands(){
        varMap.clear()
        val lines : MutableList<String> = IOUtils.readLines(commandFile.inputStream(), Charset.defaultCharset())
        lines
                .filter { it -> it.reduce().toCharArray()[0] != '#' } //allows for comments
                .map { it -> it.reduce()}
                .forEach { msg ->
                    val key = msg.split(": ", limit=2)[0]
                    varMap.put(key, msg.reduce().split("$key: ")[1].toLowerCase())
                }
    }

    fun hasCommand(name: String): Boolean = varMap.containsKey(name)

    fun getCommand(commandString: String): String = if (varMap.containsKey(commandString)) {varMap[commandString]!!} else "Command doesnt exist"

    fun addCommand(name: String, msg: String){
        varMap.put(name, msg)
        saveCommands()
    }

    fun removeCommand(name: String){
        varMap.remove(name)
        saveCommands()
    }

    fun getAllCommands():Map<String, String> = varMap.toMap()

    private fun saveCommands(){
        PrintWriter(commandFile.path).close() //clear file
        val tmp = ArrayList<String>()
        val stream = commandFile.outputStream()

        varMap.keys.forEach {
            val value = varMap[it]
            tmp.add("$it: $value")
        }
        IOUtils.writeLines(tmp, null, stream, Charset.defaultCharset())
        stream.flush()
        stream.close()
    }


}