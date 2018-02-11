import sx.blah.discord.handle.impl.obj.Guild
import java.io.File
import java.io.PrintWriter

class RankFileHandler(guild: Guild){
    companion object {
        val instances = mutableMapOf<String, RankFileHandler>()
    }

    private val RankListFile = File("guilds/${guild.stringID}/role")

    private val RankList = mutableListOf<String>()

    init{
        File("./guilds/${guild.stringID}/").mkdirs()
        if (!RankListFile.exists()){
            RankListFile.createNewFile()}
        this.read()
    }

    private fun save(){
        PrintWriter(RankListFile).close()
        //clears File

        RankListFile.printWriter().use { out ->
            RankList.forEach { out.write(it) }
            out.flush()
            out.close()
        }
    }
    private fun read(){
        RankList.clear()
        RankList.addAll(RankListFile.readLines())
    }

    fun isRank(name: String) = RankList.contains(name)

    fun addRank(name: String){
        RankList.add(name)
        save()
    }

    fun removeRank(name: String){
        if (isRank(name)) RankList.remove(name)
        save()
    }


}