package io.github.blackbaroness.boilerplate.serialization.kotlinx.serializer

import io.github.blackbaroness.durationserializer.DurationFormats
import io.github.blackbaroness.durationserializer.DurationSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Duration

object DurationStringSerializer : KSerializer<Duration> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(Duration::class.qualifiedName!!, PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Duration) {
        encoder.encodeString(DurationSerializer.serialize(value, DurationFormats.mediumLengthRussian()))
    }

    override fun deserialize(decoder: Decoder): Duration {
        return DurationSerializer.deserialize(decoder.decodeString())
    }
}

object DurationBinarySerializer : SurrogateSerializer<Duration, DurationBinarySerializer.Surrogate>(
    Surrogate.serializer(),
    Duration::class
) {

    override fun toSurrogate(value: Duration) = Surrogate(
        value.seconds,
        value.nano
    )

    override fun fromSurrogate(value: Surrogate): Duration = Duration.ofSeconds(
        value.seconds,
        value.nanos.toLong()
    )

    @Serializable
    data class Surrogate(
        val seconds: Long,
        val nanos: Int,
    )
}
