package io.github.blackbaroness.boilerplate

import com.github.benmanes.caffeine.cache.Caffeine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Duration
import kotlin.random.Random

class TemporaryCodeContainer {

    private val code2entry = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofMinutes(30))
        .removalListener<Int, Entry> { _, entry, _ ->
            if (entry != null) user2code.invalidate(entry.username)
        }
        .build<Int, Entry>()

    private val user2code = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofMinutes(30))
        .build<String, Int>()

    private val userCooldown = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofSeconds(5))
        .build<String, Unit>()

    private val mutex = Mutex()

    suspend fun createCode(username: String, callback: (suspend () -> Unit)? = null): Int = mutex.withLock {
        val username = normalizeUsername(username)

        // rate limit
        if (userCooldown.getIfPresent(username) != null)
            throw TemporaryCodeRateLimitException(username)

        // drop existing code for the user
        user2code.asMap().remove(username)?.also { old ->
            code2entry.invalidate(old)
        }

        // generate unique code
        val code = generateSequence { Random.nextInt(100_000, 1_000_000) }
            .first { code2entry.getIfPresent(it) == null }

        // save
        code2entry.put(code, Entry(username, code, callback))
        user2code.put(username, code)
        userCooldown.put(username, Unit)

        code
    }

    suspend fun invalidateCode(code: Int): Entry? = mutex.withLock {
        // remove by code (listener clears reverse index)
        code2entry.asMap().remove(code)
    }

    suspend fun invalidateUser(username: String): Entry? = mutex.withLock {
        val username = normalizeUsername(username)

        // lookup code by user
        val code = user2code.asMap().remove(username) ?: return@withLock null

        // remove by code (listener clears reverse index)
        code2entry.asMap().remove(code)
    }

    private fun normalizeUsername(username: String) = username.lowercase()

    data class Entry(
        val username: String,
        val code: Int,
        val callback: (suspend () -> Unit)?,
    )

    class TemporaryCodeRateLimitException(val username: String) : Exception()
}
