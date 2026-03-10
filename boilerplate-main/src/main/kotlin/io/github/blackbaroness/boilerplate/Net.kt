package io.github.blackbaroness.boilerplate

import com.google.common.net.InetAddresses
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.Inet4Address
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import kotlin.use

@Suppress("UnstableApiUsage")
fun String.toInetAddress(): InetAddress = InetAddresses.forString(this)

fun Int.toInetAddress(): InetAddress = InetAddress.getByAddress(
    byteArrayOf(
        (this ushr 24).toByte(),
        (this ushr 16).toByte(),
        (this ushr 8).toByte(),
        this.toByte()
    )
)

val InetSocketAddress.asNiceString: String get() = "$hostString:$port"

val ipv4AddressRegex by lazy {
    Regex("^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$")
}

val String.isValidIpv4Address: Boolean
    get() = matches(ipv4AddressRegex)

suspend fun findFreePort(): Int = withContext(Dispatchers.IO) {
    val range = 20000..60000
    repeat(range.count()) {
        val port = range.random()
        try {
            ServerSocket(port).use { return@withContext port }
        } catch (_: IOException) {
            return@repeat
        }
    }

    error { "Could not find free port using range $range" }
}

fun InetAddress.toInt(): Int {
    require(this is Inet4Address) { "Only IPv4 is supported, got: $this" }
    val bytes = address
    return (bytes[0].toInt() and 0xFF shl 24) or
        (bytes[1].toInt() and 0xFF shl 16) or
        (bytes[2].toInt() and 0xFF shl 8) or
        (bytes[3].toInt() and 0xFF)
}
