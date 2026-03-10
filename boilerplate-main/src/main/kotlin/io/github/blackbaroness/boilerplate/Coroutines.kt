package io.github.blackbaroness.boilerplate

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@JvmName("delayIntRange")
suspend fun delay(range: IntRange, unit: ChronoUnit) {
    delay(range, TimeUnit.of(unit))
}

@JvmName("delayIntRange")
suspend fun delay(range: IntRange, unit: TimeUnit) {
    delayRandomRange(range) { unit.toMillis(it.toLong()) }
}

@JvmName("delayLongRange")
suspend fun delay(range: LongRange, unit: ChronoUnit) {
    delay(range, TimeUnit.of(unit))
}

@JvmName("delayLongRange")
suspend fun delay(range: LongRange, unit: TimeUnit) {
    delayRandomRange(range) { unit.toMillis(it) }
}

@JvmName("delayKotlinDuration")
suspend fun delay(range: ClosedRange<kotlin.time.Duration>) {
    delayRandomRange(range) { it.inWholeMilliseconds }
}

@JvmName("delayJavaDuration")
suspend fun delay(range: ClosedRange<Duration>) {
    delayRandomRange(range) { it.toMillis() }
}

private suspend inline fun <T : Comparable<T>> delayRandomRange(range: ClosedRange<T>, toMillis: (T) -> Long) {
    require(!range.isEmpty()) { "Delay range must not be empty" }
    delayRandomRangeMillis(toMillis(range.start), toMillis(range.endInclusive))
}

private suspend fun delayRandomRangeMillis(min: Long, max: Long) {
    require(min < max) { "Invalid delay range: $min..$max" }
    delay(Random.nextLong(min, max + 1))
}

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> Deferred<T>.getCompletedOrNull(): T? = try {
    getCompleted()
} catch (_: IllegalStateException) {
    null
}
