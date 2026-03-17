package io.github.blackbaroness.boilerplate.paper

import org.bukkit.Color as BukkitColor
import java.awt.Color as AwtColor

val BukkitColor.asAwtColor: AwtColor
    get() = AwtColor(red, green, blue, alpha)

val AwtColor.asBukkitColor: BukkitColor
    get() = BukkitColor.fromARGB(alpha, red, green, blue)
