package io.github.blackbaroness.boilerplate.serialization.kotlinx

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.*

class LocaleStringSerializer : KSerializer<Locale> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(Locale::class.qualifiedName!!, PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Locale) =
        encoder.encodeString(value.toLanguageTag())

    override fun deserialize(decoder: Decoder): Locale =
        Locale.forLanguageTag(decoder.decodeString())
}
