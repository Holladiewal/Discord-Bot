import sx.blah.discord.handle.impl.obj.Guild
import java.io.File

class NickManager(guild: Guild){
    companion object {
        val instances = mutableMapOf<String, NickManager>()
    }

    val FileClaimed = File("guilds/${guild.stringID}/")
    val FileBlocked = File("guilds/${guild.stringID}/")
    val FileQlined  = File("guilds/${guild.stringID}/")

    val ClaimedNicks = mutableMapOf<String, String>()
    val BlockedNicks = mutableListOf<String>()
    val QlinedNicks = mutableListOf<String>()



}