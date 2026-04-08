package io.github.blackbaroness.boilerplate.serialization.kotlinx.paper.keyed

import io.github.blackbaroness.boilerplate.paper.asMinimalString
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import org.bukkit.NamespacedKey
import org.bukkit.inventory.meta.trim.TrimPattern

class TrimPatternStringSerializer : KeyedSerializer<TrimPattern>(TrimPattern::class) {

    override fun resolveEntityFromKey(key: NamespacedKey): TrimPattern {
        return RegistryAccess.registryAccess()
            .getRegistry(RegistryKey.TRIM_PATTERN)
            .get(key)
            ?: throw IllegalArgumentException("Unknown trim pattern '${key.asMinimalString}'")
    }
}
