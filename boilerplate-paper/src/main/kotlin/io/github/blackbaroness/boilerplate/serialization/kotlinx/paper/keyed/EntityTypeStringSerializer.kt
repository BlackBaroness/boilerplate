package io.github.blackbaroness.boilerplate.serialization.kotlinx.paper.keyed

import io.github.blackbaroness.boilerplate.paper.asMinimalString
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.entity.EntityType

class EntityTypeStringSerializer : KeyedSerializer<EntityType>(EntityType::class) {

    override fun resolveEntityFromKey(key: NamespacedKey): EntityType {
        return Registry.ENTITY_TYPE.get(key)
            ?: throw IllegalArgumentException("Unknown entity type '${key.asMinimalString}'")
    }
}
