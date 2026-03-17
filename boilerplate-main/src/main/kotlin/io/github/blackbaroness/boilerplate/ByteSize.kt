package io.github.blackbaroness.boilerplate

@JvmInline
value class ByteSize(val bytes: Long) {

    override fun toString(): String {
        if (bytes == 0L) return "0 B"

        val (unitName, divisor) = when {
            bytes >= GIBIBYTE -> "GiB" to GIBIBYTE
            bytes >= MEBIBYTE -> "MiB" to MEBIBYTE
            bytes >= KIBIBYTE -> "KiB" to KIBIBYTE
            else -> "B" to BYTE
        }

        val value = bytes.toDouble() / divisor
        val str = "%.2f".format(value)
            .trimEnd('0')
            .trimEnd('.')
            .trimEnd(',')

        return "$str $unitName"
    }

    companion object {
        const val BYTE: Long = 1
        const val KIBIBYTE: Long = 1024
        const val MEBIBYTE: Long = 1024 * KIBIBYTE
        const val GIBIBYTE: Long = 1024 * MEBIBYTE

        private val regex = Regex("""(\d+)\s*(b|kib|kb|mib|mb|gib|gb)?""", RegexOption.IGNORE_CASE)

        fun parse(input: String): ByteSize {
            val match = regex.matchEntire(input.trim())
                ?: throw IllegalArgumentException("Invalid byte size format: $input")
            val (amountStr, unitRaw) = match.destructured
            val amount = amountStr.toLongOrNull()
                ?: throw IllegalArgumentException("Invalid numeric value: $amountStr")
            if (amount < 0) throw IllegalArgumentException("Byte size cannot be negative: $input")

            val multiplier = when (unitRaw.lowercase()) {
                "", "b" -> BYTE
                "kib", "kb" -> KIBIBYTE
                "mib", "mb" -> MEBIBYTE
                "gib", "gb" -> GIBIBYTE
                else -> error("Unknown byte unit: $unitRaw")
            }
            return ByteSize(amount * multiplier)
        }

        fun fromKibibytes(kib: Long) = ByteSize(kib * KIBIBYTE)
        fun fromMebibytes(mib: Long) = ByteSize(mib * MEBIBYTE)
        fun fromGibibytes(gib: Long) = ByteSize(gib * GIBIBYTE)
    }
}
