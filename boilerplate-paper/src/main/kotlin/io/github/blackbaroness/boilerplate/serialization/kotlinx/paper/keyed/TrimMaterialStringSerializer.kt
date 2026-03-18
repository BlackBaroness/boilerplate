package io.github.blackbaroness.boilerplate.serialization.kotlinx.paper.keyed

import io.github.blackbaroness.boilerplate.paper.asMinimalString
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import org.bukkit.NamespacedKey
import org.bukkit.inventory.meta.trim.TrimMaterial

class TrimMaterialStringSerializer : KeyedSerializer<TrimMaterial>(TrimMaterial::class) {

    override fun resolveEntityFromKey(key: NamespacedKey): TrimMaterial {
        return RegistryAccess.registryAccess()
            .getRegistry(RegistryKey.TRIM_MATERIAL)
            .get(key)
            ?: throw IllegalArgumentException("Unknown trim material '${key.asMinimalString}'")
    }
}
