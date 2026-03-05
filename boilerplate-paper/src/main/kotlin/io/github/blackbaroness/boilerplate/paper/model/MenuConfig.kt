@file:Suppress("unused")

package io.github.blackbaroness.boilerplate.paper.model

import io.github.blackbaroness.boilerplate.adventure.MiniMessageComponent
import io.github.blackbaroness.boilerplate.adventure.asMiniMessageComponent
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.jetbrains.annotations.ApiStatus
import xyz.xenondevs.invui.gui.PagedGui
import xyz.xenondevs.invui.gui.structure.Markers
import xyz.xenondevs.invui.item.Item
import xyz.xenondevs.invui.item.ItemWrapper
import xyz.xenondevs.invui.window.Window
import xyz.xenondevs.invui.window.type.context.setTitle
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@ApiStatus.Obsolete
@Serializable
open class MenuConfig(
    val title: @Contextual MiniMessageComponent = "".asMiniMessageComponent,
    val structure: List<String> = listOf(),
    val customItems: Map<Char, ItemTemplate> = mapOf(),
    val templates: Map<Char, ItemTemplate> = mapOf(),
)

@OptIn(ExperimentalContracts::class)
inline fun Window.Builder.Normal.Single.import(
    config: MenuConfig,
    guiModifier: PagedGui.Builder<Item>.() -> Unit,
): PagedGui<Item> {
    contract {
        callsInPlace(guiModifier, InvocationKind.EXACTLY_ONCE)
    }
    setTitle(config.title.asComponent())
    val gui = config.createPagedGui().apply(guiModifier).build()
    setGui(gui)
    return gui
}

fun MenuConfig.createPagedGui(): PagedGui.Builder<Item> = PagedGui.items().apply {
    setStructure(*this@createPagedGui.structure.toTypedArray())
    addIngredient('.', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
    customItems.forEach { (key, item) ->
        addIngredient(key, ItemWrapper(item.unsafeItem))
    }
}


