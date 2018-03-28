import org.apache.commons.io.IOUtils
import sx.blah.discord.handle.impl.obj.Guild
import java.io.File
import java.io.PrintWriter
import java.nio.charset.Charset
import java.util.*

/**
 * Created by beepbeat/holladiewal on 26.01.2018.
 */

class CommandHandler(val guild: Guild) {

    companion object {
        val instances = mutableMapOf<String, CommandHandler>()
    }

    fun hasCommand(name: String): Boolean = DatabaseManagerHolder.getForGuild(guild.stringID).hasCommand(name)

    fun getCommand(commandString: String): String =
            DatabaseManagerHolder.getForGuild(guild.stringID).getCommand(commandString.toLowerCase())

    fun addCommand(name: String, msg: String){
        DatabaseManagerHolder.getForGuild(guild.stringID).setCommand(name, msg)
    }

    fun removeCommand(name: String){
        DatabaseManagerHolder.getForGuild(guild.stringID).deleteCommand(name)
    }

    fun getAllCommands():Map<String, String> = DatabaseManagerHolder.getForGuild(guild.stringID).getAllCommands()

}