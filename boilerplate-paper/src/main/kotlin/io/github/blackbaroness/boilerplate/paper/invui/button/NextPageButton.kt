package io.github.blackbaroness.boilerplate.paper.invui.button

import io.github.blackbaroness.boilerplate.paper.model.ItemTemplate
import org.bukkit.Sound
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Obsolete
class NextPageButton(
    pagePresentTemplate: ItemTemplate,
    pageAbsentTemplate: ItemTemplate,
    sound: Sound? = null,
) : PageButton(pagePresentTemplate, pageAbsentTemplate, sound) {

    override val displayedTargetPage: Int
        get() = gui.currentPage + 2

    override fun canMove(): Boolean {
        return gui.hasNextPage()
    }

    override fun move() {
        gui.goForward()
    }

}
