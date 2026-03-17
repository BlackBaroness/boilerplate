package io.github.blackbaroness.boilerplate

import java.util.*
import kotlin.random.Random

private val DEFAULT_CHARSET: CharArray =
    ("abcdefghijklmnopqrstuvwxyz" +
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
        "0123456789").toCharArray()

fun Random.nextString(length: Int, charset: CharArray = DEFAULT_CHARSET): String {
    require(length >= 0) { "Length must be non-negative" }
    require(charset.isNotEmpty()) { "Charset must not be empty" }
    return buildString(length) {
        repeat(length) {
            append(charset.random(this@nextString))
        }
    }
}

fun <T> Random.oneOf(first: T, second: T, third: T): T = when (nextInt(3)) {
    0 -> first
    1 -> second
    2 -> third
    else -> throw IllegalStateException()
}

fun Random.nextUuid(): UUID {
    return UUID(nextLong(), nextLong())
}
