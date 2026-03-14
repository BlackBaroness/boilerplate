package io.github.blackbaroness.boilerplate.bungeecord

import io.github.blackbaroness.boilerplate.Boilerplate
import kotlinx.coroutines.CompletableDeferred
import net.md_5.bungee.api.Callback
import net.md_5.bungee.api.ServerPing
import net.md_5.bungee.api.config.ServerInfo

suspend fun ServerInfo.pingSuspend(protocolVersion: Int? = null): ServerPing {
    val deferred = CompletableDeferred<ServerPing>()
    val callback = Callback<ServerPing> { result, error ->
        if (result != null) {
            deferred.complete(result)
        } else if (error != null) {
            deferred.completeExceptionally(error)
        } else {
            deferred.completeExceptionally(IllegalStateException("Server ping result and error are both null"))
        }
    }

    if (protocolVersion == null) {
        ping(callback)
        return deferred.await()
    }

    val enhancedPingMethod = Boilerplate.Reflection.BungeeServerInfo_ping
    if (enhancedPingMethod == null) {
        Boilerplate.logger.warning("net.md_5.bungee.BungeeServerInfo#ping(Callback, int) is unavailable")
        ping(callback)
        return deferred.await()
    }

    enhancedPingMethod.invokeWithArguments(this, callback, protocolVersion)
    return deferred.await()
}
