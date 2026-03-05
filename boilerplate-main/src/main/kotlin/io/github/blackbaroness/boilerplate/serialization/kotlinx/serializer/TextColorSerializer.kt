package io.github.blackbaroness.boilerplate.serialization.kotlinx.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.kyori.adventure.text.format.TextColor

object TextColorIntSerializer : KSerializer<TextColor> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(TextColor::class.qualifiedName!!, PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: TextColor) {
        encoder.encodeInt(value.value())
    }

    override fun deserialize(decoder: Decoder): TextColor =
        TextColor.color(decoder.decodeInt())
}

object TextColorHexSerializer : KSerializer<TextColor> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(TextColor::class.qualifiedName!!, PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: TextColor) {
        encoder.encodeString(value.asHexString())
    }

    override fun deserialize(decoder: Decoder): TextColor {
        val hex = decoder.decodeString()
        return TextColor.fromHexString(hex)
            ?: throw IllegalArgumentException("Invalid TextColor hex: '$hex'")
    }
}

