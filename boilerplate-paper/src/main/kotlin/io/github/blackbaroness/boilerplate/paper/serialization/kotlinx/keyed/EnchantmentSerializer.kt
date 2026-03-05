package io.github.blackbaroness.boilerplate.paper.serialization.kotlinx.keyed

import io.github.blackbaroness.boilerplate.paper.asMinimalString
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.enchantments.Enchantment

object EnchantmentSerializer : KeyedSerializer<Enchantment>(Enchantment::class) {

    override fun resolveEntityFromKey(key: NamespacedKey): Enchantment {
        return Registry.ENCHANTMENT.get(key)
            ?: throw IllegalArgumentException("Unknown enchantment '${key.asMinimalString}'")
    }
}
