import sx.blah.discord.handle.impl.obj.Guild
import sx.blah.discord.handle.obj.IUser

class PermissionsManager(val guild: Guild){
    companion object {
        val instances = mutableMapOf<String, PermissionsManager>()
    }

    fun getLevel(user: IUser): Int{
        var retVal = 0
        //retVal = DatabaseManagerHolder.getForGuild(guild.stringID).getPermLevel(user.longID, false)
        user.getRolesForGuild(guild).map{ it.stringID }.forEach {
            val tmp = DatabaseManagerHolder.getForGuild(guild.stringID).getPermLevel(it.toLong(), true)
            if (tmp > retVal) retVal = tmp
        }
        if (guild.owner.stringID == user.stringID) retVal = 5
        return retVal
    }

    fun addRole(roleId: String, permClass: Int){
        DatabaseManagerHolder.getForGuild(guild.stringID).setPermLevel(roleId.toLong(), true, permClass)
    }

    fun removeRole(roleId: String){
        DatabaseManagerHolder.getForGuild(guild.stringID).deletePermLevel(roleId.toLong())
    }
}