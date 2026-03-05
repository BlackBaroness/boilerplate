package io.github.blackbaroness.boilerplate.paper.invui.button

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import com.google.inject.assistedinject.Assisted
import dev.rollczi.litecommands.annotations.inject.Inject
import io.github.blackbaroness.boilerplate.paper.playSound
import io.github.blackbaroness.boilerplate.paper.service.UserExceptionHandler
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.plugin.Plugin
import org.jetbrains.annotations.ApiStatus
import xyz.xenondevs.invui.item.Click
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.impl.AbstractItem

@ApiStatus.Obsolete
class OneTimeClickButton @Inject constructor(
    private val plugin: Plugin,
    private val userExceptionHandler: UserExceptionHandler,
    @param:Assisted private val icon: ItemProvider,
    @param:Assisted private val clickHandler: suspend (Click) -> Unit,
    @param:Assisted private val sound: Sound? = null,
) : AbstractItem() {

    interface Factory {
        fun create(
            icon: ItemProvider,
            sound: Sound? = null,
            handler: suspend (Click) -> Unit,
        ): OneTimeClickButton
    }

    private var clicked = false

    override fun getItemProvider(): ItemProvider = icon

    override fun handleClick(clickType: ClickType, player: Player, event: InventoryClickEvent) {
        if (clicked) return
        clicked = true

        if (sound != null) {
            player.playSound(sound)
        }

        plugin.launch(plugin.entityDispatcher(player)) {
            try {
                clickHandler.invoke(Click(event))
            } catch (e: Throwable) {
                userExceptionHandler.handle(player, e)
            }
        }
    }
}
