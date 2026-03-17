package io.github.blackbaroness.boilerplate.serialization.kotlinx.paper.keyed

import io.github.blackbaroness.boilerplate.paper.asMinimalString
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.potion.PotionType

class PotionTypeStringSerializer : KeyedSerializer<PotionType>(PotionType::class) {

    override fun resolveEntityFromKey(key: NamespacedKey): PotionType {
        return Registry.POTION.get(key)
            ?: throw IllegalArgumentException("Unknown potion type '${key.asMinimalString}'")
    }
}
