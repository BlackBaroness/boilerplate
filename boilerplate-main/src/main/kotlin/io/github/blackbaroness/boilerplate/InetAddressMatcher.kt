package io.github.blackbaroness.boilerplate

import inet.ipaddr.AddressStringParameters
import inet.ipaddr.IPAddressString
import inet.ipaddr.IPAddressStringParameters
import java.net.InetAddress

class InetAddressMatcher(val patterns: Set<String>) {

    private val checkers = patterns.map { IPAddressString(it, settings) }

    fun matches(address: InetAddress): Boolean {
        val input = IPAddressString(address.hostAddress, settings)
        return checkers.any { it.contains(input) }
    }

    companion object {
        private val settings = IPAddressStringParameters.Builder()
            .setRangeOptions(AddressStringParameters.RangeParameters.WILDCARD_AND_RANGE)
            .allowEmpty(false)
            .setEmptyAsLoopback(false)
            .allowMask(true)
            .allowPrefix(true)
            .allowIPv4(true)
            .allowIPv6(true)
            .toParams()
    }
}
