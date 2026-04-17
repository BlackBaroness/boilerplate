package io.github.blackbaroness.boilerplate.paper.item

import io.github.blackbaroness.boilerplate.Boilerplate
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import kotlin.math.min

val ItemStack?.isInvalid: Boolean
    get() = this == null || this.type == Material.AIR || this.amount < 1

val ItemStack?.isValid: Boolean
    get() = !isInvalid

fun ItemStack?.validate(): ItemStack? =
    takeIf { it.isValid }

fun Inventory.toMap(clone: Boolean = true): Map<Int, ItemStack?> {
    return contents.asSequence()
        .mapIndexed { slot, item -> slot to item.validate()?.let { if (clone) it.clone() else it } }
        .toMap()
}

fun HumanEntity.giveOrDrop(items: Array<out ItemStack>, allowOthersPickup: Boolean = false, willAge: Boolean = false) {
    inventory.addItem(*items).forEach { (_, item) ->
        location.world.dropItem(location, item) { droppedItem ->
            droppedItem.owner = if (allowOthersPickup) null else uniqueId
            droppedItem.setWillAge(willAge)
        }
    }
}

@JvmName("giveOrDropVararg")
fun HumanEntity.giveOrDrop(vararg items: ItemStack, allowOthersPickup: Boolean = false, willAge: Boolean = false) {
    giveOrDrop(items, allowOthersPickup = allowOthersPickup, willAge = willAge)
}

fun HumanEntity.giveOrDrop(items: Collection<ItemStack>, allowOthersPickup: Boolean = false, willAge: Boolean = false) {
    giveOrDrop(items.toTypedArray(), allowOthersPickup = allowOthersPickup, willAge = willAge)
}

fun Boilerplate.createItemStacks(reference: ItemStack, amount: Int): List<ItemStack> = buildList {
    var created = 0
    while (true) {
        val left = amount - created
        if (left < 1) break

        val thisStackSize = min(left, reference.maxStackSize)
        add(reference.clone(thisStackSize))
        created += thisStackSize
    }
}

fun ItemStack.clone(amount: Int): ItemStack {
    return clone().also { it.amount = amount }
}

inline fun ItemStack.editMetaInline(action: (ItemMeta) -> Unit) {
    val meta = itemMeta
    action.invoke(meta)
    itemMeta = meta
}

@JvmName("editMetaInlineTyped")
inline fun <reified T : ItemMeta> ItemStack.editMetaInline(action: (T) -> Unit) {
    val meta = itemMeta
    action.invoke(meta as T)
    itemMeta = meta
}
