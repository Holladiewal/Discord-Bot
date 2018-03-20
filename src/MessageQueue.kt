/**
 * Created by beepbeat/holladiewal on 20.03.2018.
 */
object MessageQueue{
    private val queue = mutableListOf<Triple<String, String, String>>()

    fun addToQueue(guildId: String, channelId: String, message: String){
        message.split("\n").forEach { queue.add(Triple(guildId, channelId, it)) }
    }

    fun sendQueue(){
        val list = mutableMapOf<String, MutableMap<String, MutableList<String>>>()
        for ((guild, channel, message) in queue) {
            list.getOrPut(guild, { mutableMapOf()})
                .getOrPut(channel, { mutableListOf()})
                    .add(message)
        }
        list.forEach { guild ->
            guild.value.forEach {
                client
                        .getGuildByID(guild.key.toLong())
                        .getChannelByID(it.key.toLong())
                        .sendMessage(it.value.joinToString(separator="") { "$it\n" })
            }
        }

        queue.clear()
    }
}