package io.github.blackbaroness.boilerplate.serialization.kotlinx.adventure

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.kyori.adventure.text.format.TextColor

class TextColorHexStringSerializer : KSerializer<TextColor> {

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
