package io.github.blackbaroness.boilerplate.serialization.kotlinx

import io.github.blackbaroness.boilerplate.ByteSize
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class ByteSizeStringSerializer : KSerializer<ByteSize> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(this::class.qualifiedName!!, PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ByteSize) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): ByteSize {
        return ByteSize.parse(decoder.decodeString())
    }
}
