package io.github.blackbaroness.boilerplate.sql

import com.zaxxer.hikari.HikariConfig
import io.github.blackbaroness.boilerplate.WRITE_ONLY_MESSAGE
import io.github.blackbaroness.boilerplate.writeOnly
import org.h2.Driver
import org.sqlite.JDBC
import java.nio.file.Path
import kotlin.time.Duration

@DslMarker
annotation class HikariDsl

inline fun HikariConfig.configure(action: HikariConfigConfigurator.() -> Unit): HikariConfig {
    HikariConfigConfigurator(this).apply(action)
    return this
}

@HikariDsl
class HikariConfigConfigurator(val wrapped: HikariConfig) {

    @get:Deprecated(message = WRITE_ONLY_MESSAGE, level = DeprecationLevel.ERROR)
    var username by writeOnly<String> { wrapped.username = it }

    @get:Deprecated(message = WRITE_ONLY_MESSAGE, level = DeprecationLevel.ERROR)
    var password by writeOnly<String> { wrapped.password = it }

    @get:Deprecated(message = WRITE_ONLY_MESSAGE, level = DeprecationLevel.ERROR)
    var poolName by writeOnly<String> { wrapped.poolName = it }

    @get:Deprecated(message = WRITE_ONLY_MESSAGE, level = DeprecationLevel.ERROR)
    var leakDetectionThreshold by writeOnly<Duration> {
        wrapped.leakDetectionThreshold = it.inWholeMilliseconds
    }

    fun sqlite(file: Path) {
        wrapped.jdbcUrl = "jdbc:sqlite:${file.toAbsolutePath()}"
        wrapped.driverClassName = JDBC::class.qualifiedName!!
    }

    fun h2(file: Path, vararg params: String) {
        wrapped.jdbcUrl = "jdbc:h2:file:${file.toAbsolutePath()}${params.joinToString(separator = "") { ";$it" }}"
        wrapped.driverClassName = Driver::class.qualifiedName!!
    }

    fun mysql(info: DatabaseConnectionInfo) {
        database("mysql", info)
        wrapped.driverClassName = com.mysql.cj.jdbc.Driver::class.qualifiedName!!

        // required since it doesn't register itself for some reason
        com.mysql.cj.jdbc.Driver()
    }

    fun mariadb(info: DatabaseConnectionInfo) {
        database("mariadb", info)
        wrapped.driverClassName = org.mariadb.jdbc.Driver::class.qualifiedName!!
    }

    fun postgresql(info: DatabaseConnectionInfo) {
        database("postgresql", info)
        wrapped.driverClassName = org.postgresql.Driver::class.qualifiedName!!
    }

    fun database(prefix: String, info: DatabaseConnectionInfo) {
        wrapped.jdbcUrl = buildString {
            append("jdbc:$prefix://${info.address}")
            if (info.port != null) append(":${info.port}")
            append("/${info.database}${normalizeArguments(info.arguments)}")
        }
        wrapped.username = info.username
        wrapped.password = info.password
    }

    fun readOnly() {
        wrapped.isReadOnly = true
        wrapped.isAutoCommit = false
    }

    fun singleConnection() {
        wrapped.maximumPoolSize = 1
    }

    private fun normalizeArguments(arguments: List<String>): String {
        if (arguments.isEmpty()) return ""
        return arguments.joinToString(prefix = "?", separator = "&")
    }

    data class DatabaseConnectionInfo(
        val address: String,
        val port: Int?,
        val database: String,
        val username: String,
        val password: String,
        val arguments: List<String> = emptyList(),
    )
}


