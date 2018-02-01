import sx.blah.discord.api.ClientBuilder
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.util.DiscordException
import java.io.File
import java.nio.file.Path

fun main(args: Array<String>) {
    val client = createClient(secret.token, true) ?: throw IllegalStateException("client login failed")
    client.dispatcher.registerListener(EventListener())
    /*  TODO
    badword filter
    url filter
    logging off all bot related actions and messages
    "nick claiming"
    link shortener
     */
}

fun createClient(token : String, login : Boolean) : IDiscordClient? {
    File("./guilds").mkdir()
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


