package io.github.blackbaroness.boilerplate.adventure

import net.kyori.adventure.text.format.TextColor
import java.awt.Color

val TextColor.asJwtColor: Color
    get() = Color(red(), green(), blue())

val Color.asTextColor: TextColor
    get() = TextColor.color(red, green, blue)
