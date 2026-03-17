package io.github.blackbaroness.boilerplate.paper

import com.github.benmanes.caffeine.cache.AsyncCacheLoader
import com.github.benmanes.caffeine.cache.Caffeine
import io.github.blackbaroness.boilerplate.Service
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.future.await
import net.luckperms.api.LuckPermsProvider
import org.bukkit.Server
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

@Singleton
class PaperPlayerNameService @Inject constructor(
    private val server: Server,
) : Service {

    private val playerNamesCache = Caffeine.newBuilder().buildAsync(object : AsyncCacheLoader<UUID, String?> {
        override fun asyncLoad(uuid: UUID, executor: Executor): CompletableFuture<String?> {
            val name = server.getOfflinePlayer(uuid).name
            if (name != null) return CompletableFuture.completedFuture(name)
            return LuckPermsProvider.get().userManager.lookupUsername(uuid)
        }
    })

    suspend fun getPlayerName(uuid: UUID): String? {
        return playerNamesCache.get(uuid).await()
    }
}
