import org.apache.commons.io.IOUtils
import sx.blah.discord.handle.impl.obj.Guild
import java.io.File
import java.io.PrintWriter
import java.nio.charset.Charset
import java.util.ArrayList

/**
 * Created by beepbeat/holladiewal on 29.01.2018.
 */
class SettingsHandler(val guild: Guild) {
    companion object {
        val instances = mutableMapOf<String, SettingsHandler>()
    }


    private val default_values = mapOf(
            Pair("commandchar", "$"),
            Pair("qlineaction", "assignrole"),
            Pair("timeoutrole", "0"),
            Pair("timeoutchannel", "0")
    )

    init{
        default_values.forEach {
            if (get(it.key) == "") set(it.key, it.value)
        }
    }

    private fun get(key: String): String {
        return DatabaseManagerHolder.getForGuild(guild.stringID).getSettingValue(key)
    }

    private fun set(key: String, value: String){
        DatabaseManagerHolder.getForGuild(guild.stringID).setSettingValue(key, value)
    }

    //private fun remove(key: String){ varMap.remove(key) }

    fun getCommandChar(): Char = get("commandchar").toCharArray()[0]
    fun setCommandChar(value: String){ set("commandchar", value) }

    fun getqlineaction(): String = get("qline")
    fun setqlineaction(action: String){
        if (listOf("kick", "ban", "assignrole").contains(action))
        set("qline", action)
        else throw UsageError()
    }

    fun getTimeoutRole(): String = get("timeoutrole")
    fun setTimeoutRole(id: String){set("timeoutrole", id)}

    fun getTimeoutChannel(): String = get("timeoutchannel")
    fun setTimeoutChannel(id: String){set("timeoutchannel", id)}


}