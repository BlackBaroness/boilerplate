package io.github.blackbaroness.boilerplate.minecraft.broker

import io.github.blackbaroness.boilerplate.Boilerplate
import io.github.blackbaroness.boilerplate.broker.impl.BaseMessageBroker
import io.github.blackbaroness.boilerplate.compressZstd
import io.github.blackbaroness.boilerplate.decompressZstd
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import kotlin.reflect.KClass

abstract class MinecraftPluginChannelMessageBroker<TRANSPORT> : BaseMessageBroker<TRANSPORT>() {

    class NoOnlinePlayerToSendMessageWith(message: String?) : Exception(message)

    @OptIn(ExperimentalSerializationApi::class)
    protected class PluginMessagePayload(
        val className: String,
        val isMessageCompressed: Boolean,
        val message: ByteArray,
    ) {

        fun toByteArray(): ByteArray {
            val output = ByteArrayOutputStream()
            DataOutputStream(output).use { out ->
                out.writeUTF(className)
                out.writeBoolean(isMessageCompressed)
                out.write(message)
            }

            val array = output.toByteArray()
            require(array.size <= Short.MAX_VALUE) { "Payload is too large: ${array.size} bytes. Max is ${Short.MAX_VALUE}" }
            return array
        }

        fun <MESSAGE : Any> toMessage(
            clazz: KClass<MESSAGE>,
            format: BinaryFormat,
            serializer: KSerializer<MESSAGE>,
        ): MESSAGE {
            require(className == clazz.qualifiedName) {
                "Invalid class: expected ${clazz.qualifiedName}, got $className"
            }

            val raw = if (isMessageCompressed) {
                Boilerplate.decompressZstd(message)
            } else {
                message
            }

            return format.decodeFromByteArray(serializer, raw)
        }

        companion object {

            fun fromByteArray(array: ByteArray) = DataInputStream(array.inputStream()).use { stream ->
                PluginMessagePayload(
                    stream.readUTF(),
                    stream.readBoolean(),
                    stream.readNBytes(stream.available())
                )
            }

            fun <T : Any> fromMessage(messageBytes: ByteArray, messageClass: KClass<T>): PluginMessagePayload {
                val compressed = Boilerplate.compressZstd(messageBytes)

                return PluginMessagePayload(
                    className = messageClass.qualifiedName!!,
                    isMessageCompressed = compressed != null,
                    message = compressed ?: messageBytes
                )
            }
        }
    }
}
