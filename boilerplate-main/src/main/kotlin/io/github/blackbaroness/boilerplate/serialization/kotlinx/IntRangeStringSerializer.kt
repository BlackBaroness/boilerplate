package io.github.blackbaroness.boilerplate.serialization.kotlinx

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object IntRangeStringSerializer : KSerializer<IntRange> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(this::class.qualifiedName!!, PrimitiveKind.STRING)

    private const val DELIMITER = ".."

    override fun serialize(encoder: Encoder, value: IntRange) {
        value.singleOrNull()?.also {
            encoder.encodeString(it.toString())
            return
        }

        encoder.encodeString(value.first.toString() + DELIMITER + value.last)
    }

    override fun deserialize(decoder: Decoder): IntRange {
        val str = decoder.decodeString()
        val split = str.split(DELIMITER)

        if (split.size == 1)
            return str.toIntOrNull()?.let { IntRange(it, it) } ?: invalidInput(str)

        if (split.size == 2) {
            val first = split[0].toIntOrNull() ?: invalidInput(str)
            val second = split[1].toIntOrNull() ?: invalidInput(str)
            return IntRange(first, second)
        }

        invalidInput(str)
    }

    private fun invalidInput(str: String): Nothing {
        throw IllegalArgumentException("'$str' is not valid int range")
    }

}
