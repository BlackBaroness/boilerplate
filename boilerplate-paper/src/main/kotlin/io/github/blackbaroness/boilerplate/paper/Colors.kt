package io.github.blackbaroness.boilerplate.paper

import org.bukkit.Color as BukkitColor
import java.awt.Color as AwtColor

val BukkitColor.asAwtColor: AwtColor
    get() = AwtColor(red, green, blue)

val AwtColor.asBukkitColor: BukkitColor
    get() = BukkitColor.fromRGB(red, green, blue)
