package io.github.blackbaroness.boilerplate.paper.service

import org.bukkit.command.CommandSender

interface UserExceptionHandler {
    suspend fun handle(user: CommandSender, error: Throwable)
}
