package io.github.blackbaroness.boilerplate

interface Service {
    suspend fun setup() {}
    suspend fun reload() {}
    suspend fun destroy() {}
}
