package io.github.blackbaroness.boilerplate.serialization.kotlinx.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.math.BigDecimal

object BigDecimalSerializer : KSerializer<BigDecimal> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(BigDecimal::class.qualifiedName!!, PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: BigDecimal) =
        encoder.encodeString(value.stripTrailingZeros().toPlainString())

    override fun deserialize(decoder: Decoder): BigDecimal {
        val input = decoder.decodeString()
        return input.trim().toBigDecimalOrNull()
            ?: throw IllegalArgumentException("'$input' is not a valid BigDecimal string")
    }
}
