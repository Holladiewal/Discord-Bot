import org.apache.commons.io.IOUtils
import sx.blah.discord.handle.impl.obj.Guild
import java.io.File
import java.io.PrintWriter
import java.nio.charset.Charset
import java.util.ArrayList

/**
 * Created by beepbeat/holladiewal on 29.01.2018.
 */
class SettingsHandler(guild: Guild) {
    companion object {
        val instances = mutableMapOf<String, SettingsHandler>()
    }


    private var file: File
    private val varMap: MutableMap<String, String> = mutableMapOf()

    init{
        File("./guilds/${guild.stringID}").mkdir()
        file = File("./guilds/${guild.stringID}/settings")
        if (!file.exists()) {
            file.createNewFile()
            varMap["commandchar"] = "$"
            this.save()
        }
        this.read()
    }

    private fun save(){
        PrintWriter(file.path).close() //clear file
        val tmp = ArrayList<String>()
        val stream = file.outputStream()

        varMap.keys.forEach {
            val value = varMap[it]
            tmp.add("$it: $value")
        }
        IOUtils.writeLines(tmp, null, stream, Charset.defaultCharset())
        stream.flush()
        stream.close()
    }

    private fun read(){
        varMap.clear()
        val lines : MutableList<String> = IOUtils.readLines(file.inputStream(), Charset.defaultCharset())
        lines
                .filter { it -> it.reduce().toCharArray()[0] != '#' } //allows for comments
                .map { it -> it.reduce()}
                .forEach { msg ->
                    val key = msg.split(": ", limit=2)[0]
                    varMap.put(key, msg.reduce().split("$key: ")[1].toLowerCase())
                }
    }

    private fun get(key: String): String = if (varMap.containsKey(key)) varMap[key]!! else throw IllegalArgumentException("There is no setting '$key' existent.")

    private fun set(key: String, value: String){ varMap.put(key, value) }

    private fun remove(key: String){ varMap.remove(key) }

    fun getCommandChar(): Char = get("commandchar").toCharArray()[0]
    fun setCommandChar(value: String){ set("commandchar", value) }


}