package io.github.blackbaroness.boilerplate.paper

import io.github.blackbaroness.boilerplate.asMinecraftTicks
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitScheduler
import kotlin.time.Duration

inline fun bukkitRunnable(crossinline action: (runnable: BukkitRunnable) -> Unit) = object : BukkitRunnable() {
    override fun run() {
        action.invoke(this)
    }
}

fun BukkitRunnable.runTaskTimer(plugin: Plugin, delay: Duration, period: Duration) =
    runTaskTimer(plugin, delay.asMinecraftTicks, period.asMinecraftTicks)

fun BukkitScheduler.runTaskTimer(plugin: Plugin, delay: Duration, period: Duration, action: () -> Unit) =
    runTaskTimer(plugin, delay.asMinecraftTicks, period.asMinecraftTicks, action)

fun BukkitScheduler.runTaskTimer(plugin: Plugin, delay: Long, period: Long, action: () -> Unit) =
    runTaskTimer(plugin, action, delay, period)
