package io.github.blackbaroness.boilerplate.serialization.kotlinx.adventure

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.kyori.adventure.text.format.TextColor

class TextColorIntSerializer : KSerializer<TextColor> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(TextColor::class.qualifiedName!!, PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: TextColor) {
        encoder.encodeInt(value.value())
    }

    override fun deserialize(decoder: Decoder): TextColor =
        TextColor.color(decoder.decodeInt())
}
