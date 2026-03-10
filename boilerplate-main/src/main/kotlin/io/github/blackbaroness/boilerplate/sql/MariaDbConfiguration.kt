package io.github.blackbaroness.boilerplate.sql

import kotlinx.serialization.Serializable

@Serializable
data class MariaDbConfiguration(
    val address: String = "localhost",
    val port: Int = 3306,
    val database: String = "mydatabase",
    val user: String = "user",
    val password: String = "password",
    val parameters: List<String> = listOf(),
)
