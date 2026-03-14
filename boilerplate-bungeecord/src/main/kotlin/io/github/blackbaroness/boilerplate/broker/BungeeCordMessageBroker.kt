package io.github.blackbaroness.boilerplate.broker

import io.github.blackbaroness.boilerplate.bungeecord.eventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.cbor.Cbor
import net.md_5.bungee.api.connection.Connection
import net.md_5.bungee.api.event.PluginMessageEvent
import net.md_5.bungee.api.plugin.Plugin
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import kotlin.reflect.KClass

@OptIn(ExperimentalSerializationApi::class)
class BungeeCordMessageBroker(
    private val plugin: Plugin,
    private val debug: Boolean = false,
    private val format: BinaryFormat = Cbor,
) : MinecraftPluginChannelMessageBroker<Connection>() {

    override suspend fun <MESSAGE : Any> publish(
        topic: String,
        message: MESSAGE,
        messageClass: KClass<MESSAGE>,
        serializer: KSerializer<MESSAGE>?,
    ) {
        val resolvedSerializer = format.findSerializer(messageClass, serializer)
        val encoded = format.encodeToByteArray(resolvedSerializer, message)
        val payload = PluginMessagePayload.fromMessage(encoded, messageClass)
        val payloadEncoded = payload.toByteArray()

        val packet = ByteArrayOutputStream().use { outer ->
            DataOutputStream(outer).use { out ->
                out.writeUTF("Forward")
                out.writeUTF("ALL")
                out.writeUTF(topic)
                out.writeShort(payloadEncoded.size)
                out.write(payloadEncoded)
            }
            outer.toByteArray()
        }

        val player = plugin.proxy.players.firstOrNull()
            ?: throw NoOnlinePlayerToSendMessageWith("Cannot publish to '$topic': no online players online")

        logDebug { "Sending to '$topic': ${messageClass.simpleName}($message), encoded size = ${payloadEncoded.size}" }
        player.sendData("BungeeCord", packet)
    }

    override fun <MESSAGE : Any> subscribe(
        topic: String,
        messageClass: KClass<MESSAGE>,
        serializer: KSerializer<MESSAGE>?,
    ): Flow<ReceivedMessage<Connection, MESSAGE>> = callbackFlow {
        val listener = plugin.eventListener<PluginMessageEvent> { event ->
            try {
                if (event.tag != "BungeeCord") {
                    logDebug { "Ignored: tag=${event.tag}, expected='BungeeCord'" }
                    return@eventListener
                }

                val input = DataInputStream(ByteArrayInputStream(event.data))
                val instruction = input.readUTF()
                if (instruction != "Forward") {
                    logDebug { "Ignored: instruction='$instruction', expected='Forward'" }
                    return@eventListener
                }

                input.readUTF() // skip target
                val subChannel = input.readUTF()
                if (subChannel != topic) {
                    logDebug { "Ignored: subChannel='$subChannel', expected='$topic'" }
                    return@eventListener
                }

                val payloadSize = input.readShort().toInt()
                val payloadBytes = ByteArray(payloadSize)
                input.readFully(payloadBytes)

                val payload = PluginMessagePayload.fromByteArray(payloadBytes)
                if (payload.className != messageClass.qualifiedName) {
                    logDebug {
                        "Ignored: className mismatch, received='${payload.className}', expected='${messageClass.qualifiedName}'"
                    }
                    return@eventListener
                }

                val resolvedSerializer = format.findSerializer(messageClass, serializer)
                val message = payload.toMessage(messageClass, format, resolvedSerializer)

                logDebug {
                    "Accepted: from=${event.sender}, type=${messageClass.simpleName}, message=$message"
                }

                trySend(ReceivedMessage(event.sender, message))
            } catch (e: Throwable) {
                logDebug(e) { "Handler of $topic: exception thrown during deserialization or dispatch" }
            }
        }

        awaitClose {
            logDebug { "Unsubscribed from '$topic'" }
            listener.close()
        }
    }

    private inline fun logDebug(throwable: Throwable? = null, crossinline message: () -> String) {
        if (!debug) return
        plugin.logger.warning(
            buildString {
                append("[BROKER] ${message.invoke()}")
                if (throwable != null) {
                    appendLine().append(throwable.stackTraceToString())
                }
            }
        )
    }
}
