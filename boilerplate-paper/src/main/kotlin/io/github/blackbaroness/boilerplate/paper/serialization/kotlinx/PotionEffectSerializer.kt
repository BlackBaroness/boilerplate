package io.github.blackbaroness.boilerplate.paper.serialization.kotlinx

import io.github.blackbaroness.boilerplate.serialization.kotlinx.serializer.SurrogateSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object PotionEffectSerializer : SurrogateSerializer<PotionEffect, PotionEffectSerializer.Surrogate>(
    Surrogate.serializer(),
    PotionEffect::class
) {

    override fun toSurrogate(value: PotionEffect) = Surrogate(
        value.type,
        value.duration,
        value.amplifier,
        value.isAmbient,
        value.hasParticles(),
        value.hasIcon()
    )

    override fun fromSurrogate(value: Surrogate): PotionEffect = PotionEffect(
        value.type,
        value.duration,
        value.amplifier,
        value.ambient,
        value.particles,
        value.icon
    )

    @Serializable
    data class Surrogate(
        val type: @Contextual PotionEffectType,
        val duration: Int,
        val amplifier: Int,
        val ambient: Boolean,
        val particles: Boolean,
        val icon: Boolean,
    )
}
