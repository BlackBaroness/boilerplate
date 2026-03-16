package io.github.blackbaroness.boilerplate

import java.math.BigDecimal
import java.math.BigInteger

val BigDecimal.isPositive
    get() = this > BigDecimal.ZERO

val BigDecimal.isNegative
    get() = this < BigDecimal.ZERO

val BigDecimal.isZero
    get() = this == BigInteger.ZERO

fun BigDecimal.multiply(value: Double): BigDecimal = multiply(value.toBigDecimal())

operator fun BigDecimal.times(value: Double): BigDecimal = multiply(value)

fun BigDecimal.multiply(value: Int): BigDecimal = multiply(value.toBigDecimal())

operator fun BigDecimal.times(value: Int) = multiply(value)

fun Iterable<BigDecimal>.sum(): BigDecimal {
    var sum: BigDecimal = BigDecimal.ZERO
    for (element in this) {
        sum += element
    }
    return sum
}

fun Sequence<BigDecimal>.sum(): BigDecimal {
    var sum: BigDecimal = BigDecimal.ZERO
    for (element in this) {
        sum += element
    }
    return sum
}
