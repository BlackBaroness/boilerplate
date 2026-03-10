package io.github.blackbaroness.boilerplate.minecraft.service

import java.util.*

interface PlayerNameService {
    suspend fun getPlayerName(uuid: UUID): String?
}
