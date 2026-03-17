package io.github.blackbaroness.boilerplate

import java.math.BigInteger

val BigInteger.isNegative
    get() = this < BigInteger.ZERO

val BigInteger.isPositive
    get() = this > BigInteger.ZERO

val BigInteger.isZero
    get() = this == BigInteger.ZERO
