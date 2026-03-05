package io.github.blackbaroness.boilerplate.paper.serialization.kotlinx

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.potion.PotionEffectType

object PotionEffectTypeSerializer : KSerializer<PotionEffectType> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(PotionEffectType::class.qualifiedName!!, PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: PotionEffectType) =
        encoder.encodeString(value.name)

    override fun deserialize(decoder: Decoder): PotionEffectType {
        val string = decoder.decodeString()
        return PotionEffectType.getByName(string)
            ?: throw IllegalArgumentException("Unknown potion effect type '$string'")
    }
}
