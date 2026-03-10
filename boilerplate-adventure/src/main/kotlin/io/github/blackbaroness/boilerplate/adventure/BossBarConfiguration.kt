package io.github.blackbaroness.boilerplate.adventure

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

@Serializable
data class BossBarConfiguration(
    val content: @Contextual MiniMessageComponent,
    val progress: Float? = null,
    val color: BossBar.Color,
    val overlay: BossBar.Overlay,
    val flags: Set<BossBar.Flag>? = null,
) {

    fun createBossBar(vararg tagResolvers: TagResolver): BossBarReference = BossBarReference(this, tagResolvers)

    class BossBarReference(
        val config: BossBarConfiguration,
        tagResolvers: Array<out TagResolver>,
    ) {

        val bar = BossBar.bossBar(
            config.content.resolve(*tagResolvers),
            config.progress ?: 0f, config.color, config.overlay, config.flags ?: setOf()
        )

        fun updateContent(vararg tagResolvers: TagResolver) {
            bar.name(config.content.resolve(*tagResolvers))
        }

        fun updateProgressPercent(current: Number, max: Number) {
            bar.progress((current.toFloat() / max.toFloat()).coerceIn(0.0f, 1.0f))
        }
    }
}
