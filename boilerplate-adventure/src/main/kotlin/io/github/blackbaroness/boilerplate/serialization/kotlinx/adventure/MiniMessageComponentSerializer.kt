package io.github.blackbaroness.boilerplate.serialization.kotlinx.adventure

import io.github.blackbaroness.boilerplate.adventure.MiniMessageComponent
import io.github.blackbaroness.boilerplate.adventure.parseMiniMessage
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object MiniMessageComponentSerializer : KSerializer<MiniMessageComponent> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(MiniMessageComponent::class.qualifiedName!!, PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: MiniMessageComponent) =
        encoder.encodeString(value.originalString)

    override fun deserialize(decoder: Decoder): MiniMessageComponent =
        decoder.decodeString().let { MiniMessageComponent(it, it.parseMiniMessage()) }
}
