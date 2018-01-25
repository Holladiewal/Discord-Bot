import sx.blah.discord.api.ClientBuilder
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.util.DiscordException

fun main(args: Array<String>) {
    val client = createClient(secret.token, true) ?: throw IllegalStateException("client login failed")
    client.dispatcher.registerListener(EventListener())
    initAllCommands()
    readAllCommands()
    saveAllCommands()
    /*  TODO
    badword filter
    url filter
    logging off all bot related actions and messages
    "nick claiming"
    link shortener
     */
}

fun createClient(token : String, login : Boolean) : IDiscordClient? {
    val clientbuilder = ClientBuilder()
    clientbuilder.withToken(token)
    return try {
        if (login) { clientbuilder.login() }
        else { clientbuilder.build() }
    } catch (e: DiscordException) {
        e.printStackTrace()
        null
    }

}

fun String.reduce() : String{
    val tmp1 = this.trim().split(" ").toMutableList()
    tmp1.removeAll { it.trim() == "" }
    val sb = StringBuilder()
    tmp1.forEach{ sb.append(it + " ") }
    return sb.toString().trim()
}