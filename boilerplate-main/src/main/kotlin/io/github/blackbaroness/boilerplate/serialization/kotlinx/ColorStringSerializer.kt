package io.github.blackbaroness.boilerplate.serialization.kotlinx

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.awt.Color

class ColorStringSerializer : KSerializer<Color> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(Color::class.qualifiedName!!, PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Color) {
        encoder.encodeString(
            if (value.alpha == 0xFF) {
                "#%02X%02X%02X".format(value.red, value.green, value.blue)
            } else {
                "#%02X%02X%02X%02X".format(value.alpha, value.red, value.green, value.blue)
            }
        )
    }

    override fun deserialize(decoder: Decoder): Color {
        val hex = decoder.decodeString().removePrefix("#")
        return when (hex.length) {
            6 -> {
                val rgb = hex.toInt(16)
                Color(rgb or (0xFF shl 24), true)
            }

            8 -> {
                val argb = hex.toLong(16).toInt()
                Color(argb, true)
            }

            else -> throw IllegalArgumentException(
                "Hex color must be 6 or 8 digits, was ‘$hex’"
            )
        }
    }
}
