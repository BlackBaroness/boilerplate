package io.github.blackbaroness.boilerplate.paper

import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Player

fun Player.playSound(location: Location, sound: Sound) =
    playSound(location, sound, 1f, 1f)

fun Player.playSound(sound: Sound) =
    playSound(location, sound)

fun Location.playSound(sound: Sound) =
    world.playSound(this, sound, 1f, 1f)
