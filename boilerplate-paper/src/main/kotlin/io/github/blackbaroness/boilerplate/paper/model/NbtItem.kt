package io.github.blackbaroness.boilerplate.paper.model

import de.tr7zw.nbtapi.NBT

class NbtItem(val nbtString: String) {
    val itemUnsafe by lazy { NBT.itemStackFromNBT(NBT.parseNBT(nbtString))!! }
    val itemSafe get() = itemUnsafe.clone()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NbtItem

        if (nbtString != other.nbtString) return false
        if (itemUnsafe != other.itemUnsafe) return false

        return true
    }

    override fun hashCode(): Int {
        var result = nbtString.hashCode()
        result = 31 * result + itemUnsafe.hashCode()
        return result
    }
}
