package io.github.blackbaroness.boilerplate.serialization.kotlinx.paper.keyed

import io.github.blackbaroness.boilerplate.paper.asMinimalString
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment

class EnchantmentStringSerializer : KeyedSerializer<Enchantment>(Enchantment::class) {

    override fun resolveEntityFromKey(key: NamespacedKey): Enchantment {
        return RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(key)
            ?: throw IllegalArgumentException("Unknown enchantment '${key.asMinimalString}'")
    }
}
