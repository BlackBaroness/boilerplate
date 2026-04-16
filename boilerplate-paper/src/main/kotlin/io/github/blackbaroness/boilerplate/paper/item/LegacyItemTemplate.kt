package io.github.blackbaroness.boilerplate.paper.item

import com.destroystokyo.paper.profile.ProfileProperty
import io.github.blackbaroness.boilerplate.Boilerplate
import io.github.blackbaroness.boilerplate.adventure.parseMiniMessage
import io.github.blackbaroness.boilerplate.paper.setDisplayName
import io.github.blackbaroness.boilerplate.paper.setLore
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

@Suppress("DEPRECATION")
@Serializable
sealed class LegacyItemTemplate : ItemStackProvider {

    abstract val material: @Contextual Material
    abstract val amount: Int?
    abstract val name: String?
    abstract val lore: List<String>?
    abstract val enchantments: Map<@Contextual Enchantment, Int>?
    abstract val customModelData: Int?
    abstract val flags: Set<ItemFlag>?

    override val cachedItem by lazy { createItem(TagResolver.empty()) }
    override fun createItem() = cachedItem.clone()

    fun createItem(
        tagResolver: TagResolver,
        miniMessage: MiniMessage = MiniMessage.miniMessage(),
    ): ItemStack {
        val item = ItemStack(material)
        modifyItem(item, miniMessage, tagResolver)
        return item
    }

    fun createItem(
        tagResolver: TagResolver,
        vararg tagResolvers: TagResolver,
        miniMessage: MiniMessage = MiniMessage.miniMessage(),
    ): ItemStack {
        val resolver = TagResolver.builder()
        resolver.resolver(tagResolver)
        tagResolvers.forEach { resolver.resolver(it) }
        return createItem(tagResolver = resolver.build(), miniMessage = miniMessage)
    }

    fun createItem(
        tagResolvers: Iterable<TagResolver>,
        miniMessage: MiniMessage = MiniMessage.miniMessage(),
    ): ItemStack {
        return createItem(tagResolver = TagResolver.resolver(tagResolvers), miniMessage = miniMessage)
    }

    fun createItem(
        tagResolvers: Array<TagResolver>,
        miniMessage: MiniMessage = MiniMessage.miniMessage(),
    ): ItemStack {
        val resolver = TagResolver.builder()
        tagResolvers.forEach { resolver.resolver(it) }
        return createItem(tagResolver = resolver.build(), miniMessage = miniMessage)
    }

    protected open fun modifyItem(item: ItemStack, miniMessage: MiniMessage, tagResolver: TagResolver) {
        amount?.also { value ->
            item.amount = value
        }

        item.editMetaInline { meta ->
            name?.also { value ->
                Boilerplate.setDisplayName(meta, value.parseMiniMessage(tagResolver, miniMessage))
            }

            lore?.also { value ->
                Boilerplate.setLore(meta, value.map { it.parseMiniMessage(tagResolver, miniMessage) })
            }

            customModelData?.also { value ->
                meta.setCustomModelData(value)
            }
        }

        enchantments?.also { value ->
            item.addUnsafeEnchantments(value)
        }

        flags?.also { value ->
            item.addItemFlags(*value.toTypedArray())
        }
    }

    @SerialName("normal")
    @Serializable
    data class Normal(
        override val material: @Contextual Material,
        override val amount: Int? = null,
        override val name: String? = null,
        override val lore: List<String>? = null,
        override val enchantments: Map<@Contextual Enchantment, Int>? = null,
        override val customModelData: Int? = null,
        override val flags: Set<ItemFlag>? = null,
    ) : LegacyItemTemplate()


    @SerialName("player-head")
    @Serializable
    data class PlayerHead(
        val texture: String? = null,
        override val amount: Int? = null,
        override val name: String? = null,
        override val lore: List<String>? = null,
        override val enchantments: Map<@Contextual Enchantment, Int>? = null,
        override val customModelData: Int? = null,
        override val flags: Set<ItemFlag>? = null,
    ) : LegacyItemTemplate() {
        @Transient
        override val material = Material.PLAYER_HEAD

        override fun modifyItem(item: ItemStack, miniMessage: MiniMessage, tagResolver: TagResolver) {
            super.modifyItem(item, miniMessage, tagResolver)

            item.editMetaInline<SkullMeta> { meta ->
                texture?.also { value ->
                    meta.playerProfile = Bukkit.createProfile(UUID.randomUUID()).apply {
                        setProperty(ProfileProperty("textures", value))
                    }
                }
            }
        }
    }
}
