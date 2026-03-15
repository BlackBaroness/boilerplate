package io.github.blackbaroness.boilerplate.serialization.kotlinx.paper.keyed

import io.github.blackbaroness.boilerplate.paper.asMinimalString
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.attribute.Attribute

class AttributeStringSerializer : KeyedSerializer<Attribute>(Attribute::class) {

    override fun resolveEntityFromKey(key: NamespacedKey): Attribute {
        return Registry.ATTRIBUTE.get(key)
            ?: throw IllegalArgumentException("Unknown attribute '${key.asMinimalString}'")
    }
}
