package io.github.blackbaroness.boilerplate.serialization.kotlinx

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalQueries

object LocalTimeStringSerializer : KSerializer<LocalTime> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(LocalTime::class.qualifiedName!!, PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalTime) =
        encoder.encodeString(DateTimeFormatter.ISO_LOCAL_TIME.format(value))

    override fun deserialize(decoder: Decoder): LocalTime {
        val value = decoder.decodeString()
        return try {
            DateTimeFormatter.ISO_LOCAL_TIME.parse(value, TemporalQueries.localTime())
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to parse LocalTime: $value", e)
        }
    }
}
