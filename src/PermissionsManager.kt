import sx.blah.discord.handle.impl.obj.Guild
import java.io.File
import java.io.PrintStream

class PermissionsManager(guild: Guild){
    companion object {
        val instances = mutableMapOf<String, PermissionsManager>()
    }

    val FileClass1 = File("./guilds/${guild.stringID}/permissions/Class1")
    val FileClass2 = File("./guilds/${guild.stringID}/permissions/Class2")
    val FileClass3 = File("./guilds/${guild.stringID}/permissions/Class3")
    val FileClass4 = File("./guilds/${guild.stringID}/permissions/Class4")

    val class1Roles = mutableListOf<String>()
    val class2Roles = mutableListOf<String>()
    val class3Roles = mutableListOf<String>()
    val class4Roles = mutableListOf<String>()

    init{
        File("./guilds/${guild.stringID}/permissions").mkdirs()
        if (!FileClass1.exists()){FileClass1.createNewFile()}
        if (!FileClass2.exists()){FileClass2.createNewFile()}
        if (!FileClass3.exists()){FileClass3.createNewFile()}
        if (!FileClass4.exists()){FileClass4.createNewFile()}
        this.read()
    }

    private fun save(){
        PrintStream(FileClass1).close()
        FileClass1.bufferedWriter().use { out ->
            class1Roles.forEach {
                out.write(it)
                out.newLine()
            }
            out.flush()
            out.close()
        }

        PrintStream(FileClass2).close()
        FileClass2.bufferedWriter().use { out ->
            class2Roles.forEach {
                out.write(it)
                out.newLine()
            }
            out.flush()
            out.close()
        }

        PrintStream(FileClass3).close()
        FileClass3.bufferedWriter().use { out ->
            class3Roles.forEach {
                out.write(it)
                out.newLine()
            }
            out.flush()
            out.close()
        }

        PrintStream(FileClass4).close()
        FileClass4.bufferedWriter().use { out ->
            class4Roles.forEach {
                out.write(it)
                out.newLine()
            }
            out.flush()
            out.close()
        }
    }

    private fun read(){
        class1Roles.clear()
        class1Roles.addAll(FileClass1.readLines())

        class2Roles.clear()
        class2Roles.addAll(FileClass2.readLines())

        class3Roles.clear()
        class3Roles.addAll(FileClass3.readLines())

        class4Roles.clear()
        class4Roles.addAll(FileClass4.readLines())
    }


}