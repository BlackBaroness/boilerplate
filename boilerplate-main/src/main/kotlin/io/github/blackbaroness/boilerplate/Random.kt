package io.github.blackbaroness.boilerplate

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
