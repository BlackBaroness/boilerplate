package io.github.blackbaroness.boilerplate.serialization.kotlinx

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.math.BigInteger

object BigIntegerStringSerializer : KSerializer<BigInteger> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(BigInteger::class.qualifiedName!!, PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: BigInteger) =
        encoder.encodeString(value.toString())

    override fun deserialize(decoder: Decoder): BigInteger {
        val input = decoder.decodeString()
        return input.trim().toBigIntegerOrNull()
            ?: throw IllegalArgumentException("'$input' is not a valid BigInteger string")
    }
}
