package io.github.blackbaroness.boilerplate.bungeecord

import io.github.blackbaroness.boilerplate.Boilerplate
import net.md_5.bungee.api.Callback
import net.md_5.bungee.api.ProxyServer
import java.lang.invoke.MethodHandles

val Boilerplate.Reflection.BungeeServerInfo_ping by lazy {
    ProxyServer::class.java.classLoader.loadClass("net.md_5.bungee.BungeeServerInfo")
        ?.getDeclaredMethod("ping", Callback::class.java, Int::class.javaPrimitiveType)
        ?.let { MethodHandles.lookup().unreflect(it) }
}
