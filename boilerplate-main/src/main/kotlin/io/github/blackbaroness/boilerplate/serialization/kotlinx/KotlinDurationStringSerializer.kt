package io.github.blackbaroness.boilerplate.serialization.kotlinx

import io.github.blackbaroness.durationserializer.DurationFormats
import io.github.blackbaroness.durationserializer.format.DurationFormat
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.Duration
import kotlin.time.toJavaDuration
import kotlin.time.toKotlinDuration

class KotlinDurationStringSerializer(
    serializeFormat: DurationFormat = DurationFormats.mediumLengthRussian(),
    deserializeFormats: Array<DurationFormat> = DurationFormats.allBundled(),
) : KSerializer<Duration> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(this::class.qualifiedName!!, PrimitiveKind.STRING)

    private val serializer = DurationStringSerializer(serializeFormat, deserializeFormats)

    override fun serialize(encoder: Encoder, value: Duration) {
        serializer.serialize(encoder, value.toJavaDuration())
    }

    override fun deserialize(decoder: Decoder): Duration {
        return serializer.deserialize(decoder).toKotlinDuration()
    }
}
