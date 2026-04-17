@file:ApiStatus.Obsolete

package io.github.blackbaroness.boilerplate.paper.menu

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import io.github.blackbaroness.boilerplate.adventure.MiniMessageComponent
import io.github.blackbaroness.boilerplate.paper.item.ItemStackProvider
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

fun Gui.Builder<*, *>.ingredient(char: Char, item: Item) {
    addIngredient(char, item)
}

fun Gui.Builder<*, *>.ingredient(char: Char, item: ItemProvider) {
    addIngredient(char, item)
}

fun Gui.Builder<*, *>.ingredient(char: Char, item: ItemStackProvider) {
    addIngredient(char, item.cachedItem)
}

@JvmName("ingredientItem")
inline fun Gui.Builder<*, *>.ingredient(char: Char, provider: () -> Item) {
    ingredient(char, provider.invoke())
}

@JvmName("ingredientItemProvider")
inline fun Gui.Builder<*, *>.ingredient(char: Char, provider: () -> ItemProvider) {
    ingredient(char, provider.invoke())
}

@JvmName("ingredientItemStackProvider")
inline fun Gui.Builder<*, *>.ingredient(char: Char, provider: () -> ItemStackProvider) {
    ingredient(char, provider.invoke())
}

suspend inline fun <TEMPLATES, CUSTOM_ELEMENTS_PROVIDER : ItemStackProvider> Plugin.open(
    player: Player,
    template: MenuTemplate<TEMPLATES, CUSTOM_ELEMENTS_PROVIDER>,
    configure: (menu: MenuTemplate<TEMPLATES, CUSTOM_ELEMENTS_PROVIDER>, window: Window.Builder.Normal.Single, gui: PagedGui.Builder<Item>) -> Unit,
): Window {
    val window = template.createWindow { _, window ->
        window.setGui(template.createGui { _, gui ->
            configure.invoke(template, window, gui)
        })
    }
    open(player, window)
    return window
}

suspend inline fun Plugin.open(player: Player, window: Window) {
    withContext(entityDispatcher(player)) {
        window.open()
    }
}

inline fun <TEMPLATES, CUSTOM_ELEMENTS_PROVIDER : ItemStackProvider> MenuTemplate<TEMPLATES, CUSTOM_ELEMENTS_PROVIDER>.createGui(
    configure: (menu: MenuTemplate<TEMPLATES, CUSTOM_ELEMENTS_PROVIDER>, gui: PagedGui.Builder<Item>) -> Unit,
): PagedGui<Item> {
    val builder = PagedGui.items()
    builder.setStructure(*structure.toTypedArray())
    builder.addIngredient('.', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
    customElements.forEach { (key, item) ->
        builder.addIngredient(key, ItemWrapper(item.cachedItem))
    }
    configure.invoke(this, builder)
    return builder.build()
}

inline fun <TEMPLATES, CUSTOM_ELEMENTS_PROVIDER : ItemStackProvider> MenuTemplate<TEMPLATES, CUSTOM_ELEMENTS_PROVIDER>.createWindow(
    configure: (menu: MenuTemplate<TEMPLATES, CUSTOM_ELEMENTS_PROVIDER>, window: Window.Builder.Normal.Single) -> Unit,
): Window {
    val windowBuilder = Window.single()
    windowBuilder.setTitle(title)
    configure.invoke(this, windowBuilder)
    return windowBuilder.build()
}
