package io.github.blackbaroness.boilerplate.serialization.kotlinx.paper.keyed

import io.github.blackbaroness.boilerplate.paper.asMinimalString
import kotlinx.serialization.SerializationException
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.Sound

object NullAsEmptySoundStringSerializer : NullAsEmptyKeyedStringSerializer<Sound>(Sound::class) {

    override fun resolveEntityFromKey(key: NamespacedKey): Sound {
        return Registry.SOUNDS.get(key)
            ?: throw SerializationException("Unknown sound '${key.asMinimalString}'")
    }
}
