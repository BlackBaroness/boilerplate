package io.github.blackbaroness.boilerplate

import com.github.luben.zstd.Zstd

fun Boilerplate.compressZstd(payload: ByteArray, level: Int = 1, minimumLength: Int = 1500): ByteArray? {
    if (payload.size < minimumLength) {
        // the input is too small, no need to compress
        return null
    }

    val compressed = Zstd.compress(payload, level)
    val packedSize = compressed.size + 4 // we need one extra int to store the size
    if (packedSize > payload.size) {
        // compression made the array bigger
        return null
    }

    val packed = ByteArray(packedSize)
    writeIntBigEndian(packed, 0, payload.size)
    compressed.copyInto(packed, destinationOffset = 4)

    return packed
}

fun Boilerplate.decompressZstd(bytes: ByteArray): ByteArray {
    require(bytes.size >= Int.SIZE_BYTES) { "Invalid compressed format: too short" }

    val length = readIntBigEndian(bytes, 0)
    require(length >= 0) { "Invalid length: $length" }

    val compressed = bytes.copyOfRange(fromIndex = 4, toIndex = bytes.size)
    return Zstd.decompress(compressed, length)
}
