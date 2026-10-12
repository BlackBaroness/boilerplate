package io.github.blackbaroness.boilerplate

import kotlinx.serialization.Serializable
import kotlin.random.Random

@JvmInline
@Serializable
value class Chance(val percent: Double) {

    init {
        require(percent in 0.0..100.0) {
            "Chance must be between 0 and 100, got $percent"
        }
    }
}

fun Random.nextBoolean(chance: Chance): Boolean =
    nextDouble(100.0) < chance.percent
