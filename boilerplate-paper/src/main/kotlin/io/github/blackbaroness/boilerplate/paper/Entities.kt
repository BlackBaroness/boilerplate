package io.github.blackbaroness.boilerplate.paper

import io.github.blackbaroness.boilerplate.Boilerplate
import io.github.blackbaroness.boilerplate.adventure.asLegacy
import net.kyori.adventure.text.ComponentLike
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

@Suppress("DEPRECATION")
fun Player.kick(reason: ComponentLike?) {
    if (Boilerplate.isNativeAdventureApiAvailable) {
        kick(reason?.asComponent())
    } else {
        kickPlayer(reason?.asLegacy)
    }
}

fun LivingEntity.heal(amount: Double = Double.MAX_VALUE) {
    health = amount.coerceAtMost(getAttribute(Attribute.MAX_HEALTH)!!.value)
}

fun Player.feed(amount: Int = Int.MAX_VALUE) {
    foodLevel = amount.coerceIn(0, 20)
}

fun LivingEntity.clearPotionEffects() {
    activePotionEffects.forEach { removePotionEffect(it.type) }
}
