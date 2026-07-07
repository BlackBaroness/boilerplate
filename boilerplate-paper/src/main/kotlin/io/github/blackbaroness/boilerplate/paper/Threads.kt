package io.github.blackbaroness.boilerplate.paper

import org.bukkit.Bukkit

fun syncOnly() {
    check(Bukkit.isGlobalTickThread()) { "This code is sync only (currently in ${Thread.currentThread().name})" }
}
