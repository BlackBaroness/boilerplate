package io.github.blackbaroness.boilerplate.serialization.kotlinx.paper

import io.github.blackbaroness.boilerplate.paper.asAwtColor
import io.github.blackbaroness.boilerplate.paper.asBukkitColor
import io.github.blackbaroness.boilerplate.serialization.kotlinx.ColorStringSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Color

class BukkitColorStringSerializer : KSerializer<Color> {

    private val serializer = ColorStringSerializer()

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(Color::class.qualifiedName!!, PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Color) =
        serializer.serialize(encoder, value.asAwtColor)

    override fun deserialize(decoder: Decoder): Color =
        serializer.deserialize(decoder).asBukkitColor
}
