package io.github.blackbaroness.boilerplate.paper.invui

import io.github.blackbaroness.boilerplate.paper.invui.button.OneTimeClickButton
import org.bukkit.Sound
import org.jetbrains.annotations.ApiStatus
import xyz.xenondevs.invui.item.Click
import xyz.xenondevs.invui.item.ItemProvider

@ApiStatus.Obsolete
interface BoilerplateInvUiFactory {

    fun oneTimeClickButton(
        icon: ItemProvider,
        clickHandler: suspend (Click) -> Unit,
        sound: Sound? = null,
    ): OneTimeClickButton

}
