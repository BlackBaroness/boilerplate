package io.github.blackbaroness.boilerplate.adventure

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.chat.ChatType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.title.Title

@JvmInline
value class ExtendedAudience(private val origin: Audience) : Audience by origin {

    override fun sendMessage(message: Component) {
        if (message.isEmpty) return
        origin.sendMessage(message)
    }

    override fun sendMessage(message: ComponentLike) {
        if (message.isEmpty) return
        origin.sendMessage(message)
    }

    override fun sendMessage(message: Component, boundChatType: ChatType.Bound) {
        if (message.isEmpty) return
        origin.sendMessage(message, boundChatType)
    }

    override fun sendMessage(message: ComponentLike, boundChatType: ChatType.Bound) {
        if (message.isEmpty) return
        origin.sendMessage(message, boundChatType)
    }

    override fun sendActionBar(message: ComponentLike) {
        if (message.isEmpty) return
        origin.sendActionBar(message)
    }

    override fun sendActionBar(message: Component) {
        if (message.isEmpty) return
        origin.sendActionBar(message)
    }

    override fun showTitle(title: Title) {
        if (title.title().isEmpty && title.subtitle().isEmpty) return
        origin.showTitle(title)
    }
}
