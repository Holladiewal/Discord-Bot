import java.sql.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Created by beepbeat/holladiewal on 22.03.2018.
 */
object DatabaseManagerHolder {
    val instances = mutableMapOf<String, DatabaseManager>()
    class DatabaseManager(guildId: String) {
        var connection: Connection

        init{
            DriverManager.registerDriver(org.h2.Driver())
            connection = DriverManager.getConnection("jdbc:h2:./guilds/$guildId.db;AUTO_SERVER=TRUE")
            setUp_createTables()
        }

        private fun setUp_createTables(){
            val s = connection.createStatement()
            s.execute("CREATE TABLE IF NOT EXISTS commands(trigger VARCHAR, text VARCHAR)")
            s.execute("CREATE TABLE IF NOT EXISTS log(timestamp TIMESTAMP, text VARCHAR)")
            s.execute("CREATE TABLE IF NOT EXISTS settings(setting VARCHAR, value VARCHAR)")
            s.execute("CREATE TABLE IF NOT EXISTS permissions(id BIGINT, class INT, role BOOLEAN)")
        }

        protected fun query(sql: String, vararg params : Any): ResultSet {
            val prep = connection.prepareStatement(sql)
            params.forEachIndexed { index, param ->
                when (param){
                    is String -> {
                        prep.setString(index + 1, param )
                    }

                    is Int -> {
                        prep.setInt(index + 1, param)
                    }

                    is Timestamp -> {
                        prep.setTimestamp(index + 1, param)
                    }

                    is Long -> {
                        prep.setLong(index + 1, param)
                    }

                    else -> {
                        prep.setObject(index + 1, param)
                    }
                }
            }
            return prep.executeQuery()
        }

        protected fun call(sql: String, vararg params : Any): Boolean {
            val prep = connection.prepareStatement(sql)
            params.forEachIndexed { index, param ->
                when (param){
                    is String -> {
                        prep.setString(index + 1, param )
                    }

                    is Int -> {
                        prep.setInt(index + 1, param)
                    }

                    is Timestamp -> {
                        prep.setTimestamp(index + 1, param)
                    }

                    is Long -> {
                        prep.setLong(index + 1, param)
                    }

                    is Boolean -> {
                        prep.setBoolean(index + 1, param)
                    }

                    else -> {
                        prep.setObject(index + 1, param)
                    }
                }
            }
            return prep.execute()
        }


        //region command handling
        fun getCommand(name: String): String{
            if (!hasCommand(name)) return "Command doesn't exist!"
            val result = query("SELECT text FROM commands WHERE trigger=?", name)
            result.absolute(1)
            return result.getString("text")
        }

        fun hasCommand(name: String): Boolean{
            try {
                val result = query("SELECT text FROM commands WHERE trigger=?", name)
                result.absolute(1)
                result.getString("text")
            } catch (e: Exception) {
                return false
            }
            return true
        }

        fun setCommand(name: String, text: String): Boolean{
            return call("MERGE INTO COMMANDS KEY(TRIGGER) VALUES(?, ?)", name, text)
        }

        fun deleteCommand(name: String): Boolean{
            try {
                call("DELETE FROM COMMANDS WHERE TRIGGER = ?", name)
            }
            catch (e: Exception){
                return false
            }
            return true
        }

        fun getAllCommands(): Map<String, String>{
            val retVal = mutableMapOf<String, String>()
            val result = query("SELECT * FROM COMMANDS")
            while (result.next()){
                retVal.put(result.getString("trigger"), result.getString("text"))
            }
            return retVal
        }
        //endregion

        //region log
        fun log(message: String){
            //val timestamp = Timestamp(System.currentTimeMillis())
            val timestamp = Timestamp.valueOf(ZonedDateTime.now(ZoneId.of("GMT")).toLocalDateTime())
            call("INSERT INTO LOG VALUES(?,?)", timestamp, message)
        }

        fun getLog(limit: Int): Map<Timestamp, String>{
            val retVal = mutableMapOf<Timestamp, String>()
            val result = query("SELECT * FROM LOG ORDER BY 1 DESC LIMIT ?", limit)
            while(result.next()){
                retVal.put(result.getTimestamp("timestamp"), result.getString("text"))
            }
            return retVal
        }
        //endregion

        //region permissions
        fun getPermLevel(id: Long, role: Boolean = false): Int{
            val retVal: Int
            try {
                val result = query("SELECT CLASS FROM PERMISSIONS WHERE ID = ? AND ROLE = ?", id, role)
                result.absolute(1)
                retVal = result.getInt("class")
            } catch (e: Exception) {
                return 0
            }
            return retVal
        }

        fun setPermLevel(id: Long, role: Boolean, `class`: Int): Boolean {
            return call("MERGE INTO PERMISSIONS KEY(ID) VALUES(?, ?, ?)", id, `class`, role)
        }

        fun deletePermLevel(id: Long): Boolean {
            return try {
                call("DELETE FROM PERMISSIONS WHERE ID = ?", id)
            }catch (e: Exception){
                false
            }
        }
        //endregion

        //region settings
        fun getSettingValue(name: String): String{
            return try {
                val result = query("SELECT VALUE FROM SETTINGS WHERE SETTING = ?", name)
                result.absolute(1)
                result.getString("value")
            } catch (e: Exception) {
                ""
            }
        }

        fun setSettingValue(name: String, value: String){
            call("MERGE INTO SETTINGS KEY(SETTING) VALUES(?, ?)", name, value)
        }
        //endregion
    }

    fun getForGuild(guildId: String): DatabaseManager {
        return instances.getOrPut(guildId) { DatabaseManager(guildId) }
    }
}