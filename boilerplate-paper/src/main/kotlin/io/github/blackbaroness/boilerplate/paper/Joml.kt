package io.github.blackbaroness.boilerplate.paper

import org.bukkit.Location
import org.bukkit.World
import org.joml.Vector3d
import org.joml.Vector3dc
import kotlin.math.floor

val Vector3dc.blockX get() = floor(x()).toInt()

val Vector3dc.blockY get() = floor(y()).toInt()

val Vector3dc.blockZ get() = floor(z()).toInt()

fun Vector3dc.asBlock(world: World) = world.getBlockAt(blockX, blockY, blockZ)

fun Vector3dc.asLocation(world: World) = Location(world, x(), y(), z())

val Location.asVector3d get() = Vector3d(x, y, z)
