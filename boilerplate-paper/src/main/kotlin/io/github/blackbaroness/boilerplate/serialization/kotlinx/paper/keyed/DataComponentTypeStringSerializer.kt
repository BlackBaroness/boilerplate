package io.github.blackbaroness.boilerplate.serialization.kotlinx.paper.keyed

import io.github.blackbaroness.boilerplate.paper.asMinimalString
import io.papermc.paper.datacomponent.DataComponentType
import org.bukkit.NamespacedKey
import org.bukkit.Registry

@Suppress("UnstableApiUsage")
class DataComponentTypeStringSerializer : KeyedSerializer<DataComponentType>(DataComponentType::class) {

    override fun resolveEntityFromKey(key: NamespacedKey): DataComponentType {
        return Registry.DATA_COMPONENT_TYPE.get(key)
            ?: throw IllegalArgumentException("Unknown DataComponentType '${key.asMinimalString}'")
    }
}
