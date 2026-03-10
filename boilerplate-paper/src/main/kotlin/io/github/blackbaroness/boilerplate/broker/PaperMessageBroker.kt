package io.github.blackbaroness.boilerplate.broker

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.cbor.Cbor
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.messaging.PluginMessageListener
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import kotlin.reflect.KClass

@OptIn(ExperimentalSerializationApi::class)
class PaperMessageBroker(
    private val plugin: Plugin,
    private val debug: Boolean = false,
    private val format: BinaryFormat = Cbor.Default,
) : MinecraftPluginChannelMessageBroker<Player>() {

    override suspend fun <MESSAGE : Any> publish(
        topic: String,
        message: MESSAGE,
        messageClass: KClass<MESSAGE>,
        serializer: KSerializer<MESSAGE>?,
    ) {
        if (!plugin.server.messenger.isOutgoingChannelRegistered(plugin, "BungeeCord")) {
            plugin.server.messenger.registerOutgoingPluginChannel(plugin, "BungeeCord")
        }

        val resolvedSerializer = format.findSerializer(messageClass, serializer)
        val encoded = format.encodeToByteArray(resolvedSerializer, message)
        val payload = PluginMessagePayload.fromMessage(encoded, messageClass).toByteArray()

        val packet = ByteArrayOutputStream().use { byteStream ->
            DataOutputStream(byteStream).use { out ->
                out.writeUTF("Forward")
                out.writeUTF("ALL")
                out.writeUTF(topic)
                out.writeShort(payload.size)
                out.write(payload)
            }
            byteStream.toByteArray()
        }

        val player = plugin.server.onlinePlayers.firstOrNull()
            ?: throw NoOnlinePlayerToSendMessageWith("Cannot publish to '$topic': no online players online")

        logDebug { "Sending to '$topic': ${messageClass.simpleName}($message), encoded size = ${payload.size}" }
        player.sendPluginMessage(plugin, "BungeeCord", packet)
    }

    override fun <MESSAGE : Any> subscribe(
        topic: String,
        messageClass: KClass<MESSAGE>,
        serializer: KSerializer<MESSAGE>?,
    ): Flow<ReceivedMessage<Player, MESSAGE>> = callbackFlow {
        val listener = PluginMessageListener { _, player, content ->
            try {
                val input = DataInputStream(ByteArrayInputStream(content))
                val instruction = input.readUTF()
                if (instruction != "Forward") {
                    logDebug { "Ignored: instruction='$instruction', expected='Forward'" }
                    return@PluginMessageListener
                }

                input.readUTF() // skip target server
                val subChannel = input.readUTF()
                if (subChannel != topic) {
                    logDebug { "Ignored: subChannel='$subChannel', expected='$topic'" }
                    return@PluginMessageListener
                }

                val length = input.readShort().toInt()
                val payloadBytes = ByteArray(length)
                input.readFully(payloadBytes)

                val payload = PluginMessagePayload.fromByteArray(payloadBytes)
                if (payload.className != messageClass.qualifiedName) {
                    logDebug {
                        "Ignored: class mismatch. Received='${payload.className}', expected='${messageClass.qualifiedName}'"
                    }
                    return@PluginMessageListener
                }

                val resolvedSerializer = format.findSerializer(messageClass, serializer)
                val message = payload.toMessage(messageClass, format, resolvedSerializer)
                logDebug { "Accepted: from=$player, type=${messageClass.simpleName}, message=$message" }
                trySend(ReceivedMessage(player, message))
            } catch (e: Throwable) {
                logDebug(e) { "Handler of $topic: exception thrown during deserialization or dispatch" }
            }
        }

        val registration = plugin.server.messenger.registerIncomingPluginChannel(plugin, "BungeeCord", listener)

        logDebug { "Subscribed to '$topic'" }

        awaitClose {
            logDebug { "Unsubscribed from '$topic'" }
            plugin.server.messenger.unregisterIncomingPluginChannel(
                registration.plugin,
                registration.channel,
                registration.listener
            )
        }
    }

    private inline fun logDebug(throwable: Throwable? = null, message: () -> String) {
        if (!debug) return
        plugin.slF4JLogger.warn("[BROKER] ${message.invoke()}", throwable)
    }
}
