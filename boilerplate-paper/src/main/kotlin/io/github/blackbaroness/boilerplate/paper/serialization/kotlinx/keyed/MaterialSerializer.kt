package io.github.blackbaroness.boilerplate.paper.serialization.kotlinx.keyed

import io.github.blackbaroness.boilerplate.paper.asMinimalString
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Registry

object MaterialSerializer : KeyedSerializer<Material>(Material::class) {

    override fun resolveEntityFromKey(key: NamespacedKey): Material {
        return Registry.MATERIAL.get(key)
            ?: throw IllegalArgumentException("Unknown material '${key.asMinimalString}'")
    }
}
