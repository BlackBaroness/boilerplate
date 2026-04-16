package io.github.blackbaroness.boilerplate.paper.item

import de.tr7zw.nbtapi.NBT

class NbtItem(val nbtString: String) : ItemStackProvider {
    override val cachedItem by lazy { NBT.itemStackFromNBT(NBT.parseNBT(nbtString))!! }
    override fun createItem() = cachedItem.clone()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NbtItem

        if (nbtString != other.nbtString) return false
        if (cachedItem != other.cachedItem) return false

        return true
    }

    override fun hashCode(): Int {
        var result = nbtString.hashCode()
        result = 31 * result + cachedItem.hashCode()
        return result
    }
}
