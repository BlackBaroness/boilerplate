@file:ApiStatus.Obsolete

package io.github.blackbaroness.boilerplate.paper

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import io.github.blackbaroness.boilerplate.adventure.MiniMessageComponent
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.jetbrains.annotations.ApiStatus
import org.jetbrains.annotations.Contract
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.gui.PagedGui
import xyz.xenondevs.invui.gui.structure.Markers
import xyz.xenondevs.invui.item.Item
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.ItemWrapper
import xyz.xenondevs.invui.window.Window
import xyz.xenondevs.invui.window.changeTitle
import xyz.xenondevs.invui.window.type.context.setTitle

@Contract("_ -> this")
fun <B : Window.Builder<*, B>> B.setTitle(title: ComponentLike): B = setTitle(title.asComponent())

@Contract("_ -> this")
fun <B : Window.Builder<*, B>> B.setTitle(title: MiniMessageComponent, vararg resolver: TagResolver): B =
    setTitle(title.resolve(*resolver))

fun Window.changeTitle(title: ComponentLike) = changeTitle(title.asComponent())

fun PagedGui<*>.autoUpdateTitleOnPageChange(titleProvider: (currentPage: Int, totalPages: Int) -> ComponentLike) {
    addPageChangeHandler { _, newPage ->
        val title = titleProvider.invoke(newPage + 1, pageAmount).asComponent()
        for (window in findAllWindows()) {
            window.changeTitle(title)
        }
    }
}

inline fun Gui.Builder<*, *>.ingredient(char: Char, provider: () -> Item) {
    addIngredient(char, provider.invoke())
}

inline fun Gui.Builder<*, *>.ingredientProvider(char: Char, provider: () -> ItemProvider) {
    addIngredient(char, provider.invoke())
}

fun Gui.Builder<*, *>.ingredient(char: Char, item: Item) {
    addIngredient(char, item)
}

suspend inline fun <TEMPLATES> Plugin.open(
    player: Player,
    template: MenuTemplate<TEMPLATES>,
    configure: (menu: MenuTemplate<TEMPLATES>, window: Window.Builder.Normal.Single, gui: PagedGui.Builder<Item>) -> Unit,
): Window {
    val windowBuilder = Window.single()
    windowBuilder.setTitle(template.title)

    val guiBuilder = PagedGui.items()
    guiBuilder.setStructure(*template.structure.toTypedArray())
    guiBuilder.addIngredient('.', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
    template.customElements.forEach { (key, item) ->
        guiBuilder.addIngredient(key, ItemWrapper(item.cachedItem))
    }

    configure.invoke(template, windowBuilder, guiBuilder)

    windowBuilder.setGui(guiBuilder.build())
    val window = windowBuilder.build(player)
    withContext(entityDispatcher(player)) {
        window.open()
    }
    return window
}
