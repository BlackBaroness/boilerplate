package io.github.blackbaroness.boilerplate.paper.invui.button

import io.github.blackbaroness.boilerplate.paper.model.ItemTemplate
import org.bukkit.Sound
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Obsolete
class PreviousPageButton(
    pagePresentTemplate: ItemTemplate,
    pageAbsentTemplate: ItemTemplate,
    sound: Sound? = null,
) : PageButton(pagePresentTemplate, pageAbsentTemplate, sound) {

    override val displayedTargetPage
        get() = gui.currentPage

    override fun canMove(): Boolean {
        return gui.hasPreviousPage()
    }

    override fun move() {
        gui.goBack()
    }
}
