package io.github.blackbaroness.boilerplate.math.region

import org.joml.Vector3dc

interface Region {
    operator fun contains(point: Vector3dc): Boolean
}
