import sx.blah.discord.handle.impl.obj.Guild
import sx.blah.discord.handle.obj.IUser
import java.io.File
import java.io.PrintWriter

class NickManager(val guild: Guild){
    companion object {
        val instances = mutableMapOf<String, NickManager>()
    }

    val FileClaimed = File("guilds/${guild.stringID}/nicks/claimed")
    val FileBlocked = File("guilds/${guild.stringID}/nicks/blocked")
    val FileQlined  = File("guilds/${guild.stringID}/nicks/qlined")

    val ClaimedNicks = mutableMapOf<String, String>()
    val BlockedNicks = mutableListOf<String>()
    val QlinedNicks = mutableListOf<String>()

    init{
        File("./guilds/${guild.stringID}/nicks").mkdirs()
        if (!FileClaimed.exists()){FileClaimed.createNewFile()}
        if (!FileBlocked.exists()){FileBlocked.createNewFile()}
        if (!FileQlined.exists()){FileQlined.createNewFile()}
        this.read()
    }

    fun save(){
        PrintWriter(FileClaimed).close()
        PrintWriter(FileBlocked).close()
        PrintWriter(FileQlined).close()
        //clears Files

        FileClaimed.printWriter().use { out ->
            ClaimedNicks.map { "${it.key}:${it.value}" }.forEach { out.write(it) }
            out.flush()
            out.close()
        }

        FileBlocked.printWriter().use { out ->
            BlockedNicks.forEach { out.write(it) }
            out.flush()
            out.close()
        }

        FileQlined.printWriter().use { out ->
            QlinedNicks.forEach { out.write(it) }
            out.flush()
            out.close()
        }

    }
    fun read(){
        ClaimedNicks.clear()
        BlockedNicks.clear()
        QlinedNicks.clear()

        FileClaimed.readLines().forEach{ClaimedNicks.put(it.split(":", limit = 2)[0], it.split(":", limit = 2)[1])}
        BlockedNicks.addAll(FileBlocked.readLines())
        QlinedNicks.addAll(FileQlined.readLines())

    }

    fun claimNick(nick: String, user: IUser){
        ClaimedNicks.put(nick, "${user.name}#${user.discriminator}")
        save()
    }

    fun nickState(nick: String, user: IUser): String{
        if (ClaimedNicks.containsKey(nick) && !("${user.name}#${user.discriminator}" == ClaimedNicks[nick] || guild.permissions.getLevel(user) >= 4)) return "claimed"
        if (BlockedNicks.contains(nick) && guild.permissions.getLevel(user) <= 3) return "blocked"
        if (QlinedNicks.contains(nick) && guild.permissions.getLevel(user) <= 3) return "qlined"
        return "free"
    }

    fun getClaimedNickOwner(nick: String) = ClaimedNicks[nick]

    fun unclaimNick(nick: String){ ClaimedNicks.remove(nick) }

    fun blockNick(nick: String){ BlockedNicks.add(nick) }

    fun unblockNick(nick: String){ BlockedNicks.remove(nick) }

    fun qlineNick(nick: String){ QlinedNicks.add(nick) }

    fun unQlineNick(nick: String){ QlinedNicks.remove(nick) }

}