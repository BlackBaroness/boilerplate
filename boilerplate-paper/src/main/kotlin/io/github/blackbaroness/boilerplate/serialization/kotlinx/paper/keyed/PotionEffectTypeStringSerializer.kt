package io.github.blackbaroness.boilerplate.serialization.kotlinx.paper.keyed

import io.github.blackbaroness.boilerplate.paper.asMinimalString
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.potion.PotionEffectType

class PotionEffectTypeStringSerializer : KeyedSerializer<PotionEffectType>(PotionEffectType::class) {

    override fun resolveEntityFromKey(key: NamespacedKey): PotionEffectType {
        return Registry.POTION_EFFECT_TYPE.get(key)
            ?: throw IllegalArgumentException("Unknown potion effect type '${key.asMinimalString}'")
    }
}
