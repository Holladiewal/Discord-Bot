/**
 * Created by beepbeat/holladiewal on 25.12.2017.
 */
import Config.Companion.config
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.PrintWriter
import java.nio.charset.Charset
import java.util.*

val configFile = File("config")



public class Config{
    companion object {
        public val config = Config()
    }
    val varMap = mutableMapOf<String, String>()

}

enum class entries{
    token, username, channel
}

/**
 * @return true if config already existed, false when it has been initialized with defaults
 */
fun initConfig() : Boolean{
    if (!configFile.exists()) {
        configFile.createNewFile()
        //DEFAULT CONFIG
        config.varMap["${entries.token}"] = "<token here>"
        config.varMap["${entries.username}"] = "<username here>"
        config.varMap["${entries.channel}"] = "<channelname here, include '#'>"
        saveConfig()
        return false
    }
    return true
}

fun readConfig(){
    config.varMap.clear()
    val lines : MutableList<String> = IOUtils.readLines(configFile.inputStream(), Charset.defaultCharset())
    lines
            .filter { it -> it.reduce().toCharArray()[0] != '#' } //allows for comments
            .map { it -> it.reduce()}
            .forEach { msg ->
                entries.values().forEach {
                    if (msg.reduce().startsWith("$it: ")) {
                        config.varMap.put("$it", msg.reduce().split("$it: ")[1].toLowerCase())
                    }
                }
            }

    if ("${config.varMap["${entries.token}"]}" == "null"){config.varMap["${entries.token}"] = ""}

}

fun saveConfig(){
    PrintWriter(configFile.path).close()
    val tmp = ArrayList<String>()
    val stream = configFile.outputStream()

    entries.values().forEach {
        val value = config.varMap["$it"]
        if ("$it" == "token"){
            tmp.add("#NEVER EVER SHARE THIS TOKEN WITH ANYBODY")
        }

        tmp.add("$it: $value")
    }


    IOUtils.writeLines(tmp, null, stream, Charset.defaultCharset())
    stream.flush()
    stream.close()
}