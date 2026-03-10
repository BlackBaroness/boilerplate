package io.github.blackbaroness.boilerplate.adventure

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.title.Title
import net.kyori.adventure.util.Ticks
import java.time.Duration

@Serializable
data class TitleConfiguration(
    val title: @Contextual MiniMessageComponent,
    val subtitle: @Contextual MiniMessageComponent,
    val durationFadeIn: @Contextual Duration = Ticks.duration(10),
    val durationStay: @Contextual Duration = Ticks.duration(70),
    val durationFadeOut: @Contextual Duration = Ticks.duration(20),
) {

    fun createTitle(vararg tagResolvers: TagResolver): Title = Title.title(
        title.resolve(*tagResolvers).asComponent(),
        subtitle.resolve(*tagResolvers).asComponent(),
        Title.Times.times(durationFadeIn, durationStay, durationFadeOut)
    )
}
