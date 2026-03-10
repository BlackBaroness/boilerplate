package io.github.blackbaroness.boilerplate.minecraft.mojang.profile

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import io.github.blackbaroness.boilerplate.Service
import io.github.blackbaroness.boilerplate.minecraft.isOfflineUuid
import io.github.blackbaroness.boilerplate.minecraft.service.PlayerNameService
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.util.*
import kotlin.jvm.optionals.getOrNull
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

@OptIn(ExperimentalUuidApi::class)
@Singleton
class MojangPlayerProfileService @Inject constructor(
    private val playerNameService: PlayerNameService,
) : Service {

    private val httpClient = HttpClient.newBuilder()
        .followRedirects(HttpClient.Redirect.NORMAL)
        .connectTimeout(Duration.ofSeconds(5))
        .build()

    private val nameToOnlineUuidCache: Cache<String, Optional<UUID>> = Caffeine.newBuilder()
        .maximumSize(2500)
        .expireAfterWrite(Duration.ofDays(3))
        .build()

    private val onlineUuidToProfileCache: Cache<UUID, Optional<MojangPlayerProfile>> = Caffeine.newBuilder()
        .maximumSize(2500)
        .expireAfterWrite(Duration.ofDays(3))
        .build()

    suspend fun getProfile(name: String): MojangPlayerProfile? {
        return getOnlineUuid(name)?.let { getProfile(it) }
    }

    suspend fun getProfile(uuid: UUID): MojangPlayerProfile? {
        val onlineUuid = if (uuid.isOfflineUuid) {
            playerNameService.getPlayerName(uuid)?.let { getOnlineUuid(it) } ?: return null
        } else {
            uuid
        }

        onlineUuidToProfileCache.getIfPresent(onlineUuid)?.also {
            return it.getOrNull()
        }

        val profile =
            queryMojang("https://sessionserver.mojang.com/session/minecraft/profile/$onlineUuid?unsigned=false") {
                Json.decodeFromJsonElement<MojangPlayerProfile>(it)
            }


        onlineUuidToProfileCache.put(onlineUuid, Optional.ofNullable(profile))

        return profile
    }

    suspend fun getOnlineUuid(name: String): UUID? {
        nameToOnlineUuidCache.getIfPresent(name)?.also {
            return it.getOrNull()
        }

        val uuid = queryMojang("https://api.minecraftservices.com/minecraft/profile/lookup/name/$name") {
            Json.decodeFromJsonElement<LookupResult>(it).id.toJavaUuid()
        }

        nameToOnlineUuidCache.put(name, Optional.ofNullable(uuid))

        return uuid
    }

    private suspend fun <RESULT> queryMojang(url: String, action: (JsonObject) -> RESULT): RESULT? {
        while (true) {
            val request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build()

            val response = withContext(Dispatchers.IO) {
                httpClient.send(request, HttpResponse.BodyHandlers.ofString())
            }

            when (val statusCode = response.statusCode()) {
                200 -> {}
                404 -> return null
                429 -> {
                    delay(500)
                    continue
                }

                else -> throw IllegalStateException("Unexpected HTTP response code: $statusCode")
            }

            val json = Json.parseToJsonElement(response.body()).jsonObject
            if ("error" in json) throw IllegalStateException("Mojang error: $json")
            return action.invoke(json)
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    @JsonIgnoreUnknownKeys
    @Serializable
    data class LookupResult(
        val name: String,
        val id: Uuid,
    )
}
