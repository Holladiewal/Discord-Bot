import sx.blah.discord.api.ClientBuilder
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.util.DiscordException
import java.io.File
import kotlin.concurrent.timer

val client: IDiscordClient = createClient(secret.token, true) ?: throw IllegalStateException("client login failed")
fun main(args: Array<String>) {
    client.dispatcher.registerListener(EventListener())

    val timer = timer("QueueSender", true, 0, 1000){
        MessageQueue.sendQueue()
    }

    /*  TODO:
    badword filter
    url filter
    logging off all bot related actions and messages - nearly done
    "nick claiming" - nearly done
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


