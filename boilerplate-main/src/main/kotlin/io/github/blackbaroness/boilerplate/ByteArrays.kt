package io.github.blackbaroness.boilerplate

import java.util.*

fun writeLongBigEndian(array: ByteArray, offset: Int, value: Long) {
    array[offset + 0] = (value ushr 56).toByte()
    array[offset + 1] = (value ushr 48).toByte()
    array[offset + 2] = (value ushr 40).toByte()
    array[offset + 3] = (value ushr 32).toByte()
    array[offset + 4] = (value ushr 24).toByte()
    array[offset + 5] = (value ushr 16).toByte()
    array[offset + 6] = (value ushr 8).toByte()
    array[offset + 7] = value.toByte()
}

fun readLongBigEndian(array: ByteArray, offset: Int): Long {
    return ((array[offset + 0].toLong() and 0xff) shl 56) or
        ((array[offset + 1].toLong() and 0xff) shl 48) or
        ((array[offset + 2].toLong() and 0xff) shl 40) or
        ((array[offset + 3].toLong() and 0xff) shl 32) or
        ((array[offset + 4].toLong() and 0xff) shl 24) or
        ((array[offset + 5].toLong() and 0xff) shl 16) or
        ((array[offset + 6].toLong() and 0xff) shl 8) or
        ((array[offset + 7].toLong() and 0xff))
}

fun writeIntBigEndian(array: ByteArray, offset: Int, value: Int) {
    array[offset + 0] = (value ushr 24).toByte()
    array[offset + 1] = (value ushr 16).toByte()
    array[offset + 2] = (value ushr 8).toByte()
    array[offset + 3] = value.toByte()
}

fun readIntBigEndian(array: ByteArray, offset: Int): Int {
    return ((array[offset + 0].toInt() and 0xff) shl 24) or
        ((array[offset + 1].toInt() and 0xff) shl 16) or
        ((array[offset + 2].toInt() and 0xff) shl 8) or
        ((array[offset + 3].toInt() and 0xff))
}

fun UUID.toByteArray(): ByteArray {
    val array = ByteArray(16)
    writeLongBigEndian(array, 0, mostSignificantBits)
    writeLongBigEndian(array, 8, leastSignificantBits)
    return array
}

fun ByteArray.toUuid(offset: Int = 0) = UUID(
    readLongBigEndian(this, offset),
    readLongBigEndian(this, offset + 8)
)
