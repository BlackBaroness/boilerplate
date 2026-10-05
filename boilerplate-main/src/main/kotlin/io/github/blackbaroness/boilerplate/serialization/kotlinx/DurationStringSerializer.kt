package io.github.blackbaroness.boilerplate.serialization.kotlinx

import io.github.blackbaroness.durationserializer.DurationFormats
import io.github.blackbaroness.durationserializer.DurationSerializer
import io.github.blackbaroness.durationserializer.format.DurationFormat
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Duration

class DurationStringSerializer(
    private val serializeFormat: DurationFormat = DurationFormats.mediumLengthRussian(),
    private val deserializeFormats: Array<DurationFormat> = DurationFormats.allBundled(),
) : KSerializer<Duration> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(Duration::class.qualifiedName!!, PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Duration) {
        encoder.encodeString(DurationSerializer.serialize(value, serializeFormat))
    }

    override fun deserialize(decoder: Decoder): Duration {
        return DurationSerializer.deserialize(decoder.decodeString(), *deserializeFormats)
    }
}
