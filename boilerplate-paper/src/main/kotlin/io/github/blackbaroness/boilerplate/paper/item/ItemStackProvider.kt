package io.github.blackbaroness.boilerplate.paper.item

import org.bukkit.inventory.ItemStack

interface ItemStackProvider {
    val cachedItem: ItemStack
    fun createItem(): ItemStack
}
