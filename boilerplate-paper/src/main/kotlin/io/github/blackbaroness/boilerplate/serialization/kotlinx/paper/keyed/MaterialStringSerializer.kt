package io.github.blackbaroness.boilerplate.serialization.kotlinx.paper.keyed

import io.github.blackbaroness.boilerplate.paper.asMinimalString
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Registry

object MaterialStringSerializer : KeyedSerializer<Material>(Material::class) {

    override fun resolveEntityFromKey(key: NamespacedKey): Material {
        return Registry.MATERIAL.get(key)
            ?: throw IllegalArgumentException("Unknown material '${key.asMinimalString}'")
    }
}
