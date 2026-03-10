package io.github.blackbaroness.boilerplate.serialization.kotlinx.paper.keyed

import io.github.blackbaroness.boilerplate.paper.asMinimalString
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.Sound

object SoundStringSerializer : KeyedSerializer<Sound>(Sound::class) {

    override fun resolveEntityFromKey(key: NamespacedKey): Sound {
        return Registry.SOUNDS.get(key)
            ?: throw IllegalArgumentException("Unknown sound '${key.asMinimalString}'")
    }
}
