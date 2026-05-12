package io.github.blackbaroness.boilerplate.serialization.kotlinx.paper.keyed

import io.github.blackbaroness.boilerplate.Boilerplate
import io.github.blackbaroness.boilerplate.paper.asMinimalString
import io.github.blackbaroness.boilerplate.paper.isRegistryAccessApiAvailable
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.enchantments.Enchantment

class EnchantmentStringSerializer : KeyedSerializer<Enchantment>(Enchantment::class) {

    @Suppress("DEPRECATION")
    override fun resolveEntityFromKey(key: NamespacedKey): Enchantment {
        return if (Boilerplate.isRegistryAccessApiAvailable) {
            RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(key)
        } else {
            Registry.ENCHANTMENT.get(key)
        } ?: throw IllegalArgumentException("Unknown enchantment '${key.asMinimalString}'")
    }
}
