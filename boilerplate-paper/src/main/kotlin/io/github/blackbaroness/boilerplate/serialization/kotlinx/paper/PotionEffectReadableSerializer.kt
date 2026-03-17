package io.github.blackbaroness.boilerplate.serialization.kotlinx.paper

import io.github.blackbaroness.boilerplate.serialization.kotlinx.SurrogateSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class PotionEffectReadableSerializer : SurrogateSerializer<PotionEffect, PotionEffectReadableSerializer.Surrogate>(
    Surrogate.serializer(),
    PotionEffect::class
) {

    override fun toSurrogate(value: PotionEffect) = Surrogate(
        value.type,
        value.duration,
        value.amplifier,
        value.isAmbient.takeIf { !it },
        value.hasParticles().takeIf { !it },
        value.hasIcon().takeIf { !it }
    )

    override fun fromSurrogate(value: Surrogate): PotionEffect = PotionEffect(
        value.type,
        value.duration,
        value.amplifier,
        value.ambient ?: true,
        value.particles ?: true,
        value.icon ?: true
    )

    @Serializable
    data class Surrogate(
        val type: @Contextual PotionEffectType,
        val duration: Int,
        val amplifier: Int,
        val ambient: Boolean? = null,
        val particles: Boolean? = null,
        val icon: Boolean? = null,
    )
}
