package io.github.blackbaroness.boilerplate.paper

import org.bukkit.scheduler.BukkitRunnable

inline fun bukkitRunnable(crossinline action: (runnable: BukkitRunnable) -> Unit) = object : BukkitRunnable() {
    override fun run() {
        action.invoke(this)
    }
}
