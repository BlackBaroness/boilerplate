package io.github.blackbaroness.boilerplate.paper

import com.destroystokyo.paper.profile.ProfileProperty
import io.github.blackbaroness.boilerplate.adventure.parseMiniMessage
import io.papermc.paper.datacomponent.DataComponentType
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.trim.ArmorTrim
import org.bukkit.inventory.meta.trim.TrimMaterial
import org.bukkit.inventory.meta.trim.TrimPattern
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionType

@Suppress("UnstableApiUsage")
@Serializable
sealed class ItemTemplate {

    abstract val material: @Contextual Material
    abstract val amount: Int?
    abstract val name: String?
    abstract val lore: List<String>?
    abstract val tooltip: TooltipConfiguration?
    abstract val enchantments: Map<@Contextual Enchantment, Int>?
    abstract val customModelData: CustomModelDataConfiguration?
    abstract val unbreakable: Boolean?
    abstract val enchantmentGlintOverride: Boolean?
    abstract val attributeModifiers: List<AttributeConfiguration>?

    @Serializable
    data class AttributeConfiguration(
        val attribute: @Contextual Attribute,
        val modifier: @Contextual AttributeModifier,
    )

    @Serializable
    data class CustomModelDataConfiguration(
        val floats: List<Float>? = null,
        val flags: List<Boolean>? = null,
        val strings: List<String>? = null,
        val colors: List<@Contextual Color>? = null,
    )

    @Serializable
    data class TooltipConfiguration(
        val hide: Boolean? = null,
        val hiddenComponents: Set<@Contextual DataComponentType>? = null,
    )

    @Serializable
    data class TrimConfiguration(
        val material: @Contextual TrimMaterial,
        val pattern: @Contextual TrimPattern,
    ) {
        fun toArmorTrim(): ArmorTrim {
            return ArmorTrim(material, pattern)
        }
    }

    val cachedItem by lazy { createItem(TagResolver.empty()) }

    fun createItem(): ItemStack {
        return cachedItem.clone()
    }

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

        name?.also { value ->
            item.setData(
                DataComponentTypes.ITEM_NAME,
                value.parseMiniMessage(tagResolver, miniMessage)
            )
        }

        lore?.also { value ->
            item.setData(
                DataComponentTypes.LORE,
                ItemLore.lore(value.map { it.parseMiniMessage(tagResolver, miniMessage) })
            )
        }

        tooltip?.also { value ->
            val data = TooltipDisplay.tooltipDisplay().apply {
                if (value.hide != null) {
                    hideTooltip(value.hide)
                }

                if (value.hiddenComponents != null) {
                    hiddenComponents(value.hiddenComponents)
                }
            }

            item.setData(DataComponentTypes.TOOLTIP_DISPLAY, data)
        }

        enchantments?.also { value ->
            item.setData(DataComponentTypes.ENCHANTMENTS, ItemEnchantments.itemEnchantments(value))
        }

        customModelData?.also { value ->
            val data = CustomModelData.customModelData().apply {
                value.floats?.also { addFloats(it) }
                value.flags?.also { addFlags(it) }
                value.strings?.also { addStrings(it) }
                value.colors?.also { addColors(it) }
            }

            item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, data)
        }

        if (unbreakable == true) {
            item.setData(DataComponentTypes.UNBREAKABLE)
        }

        enchantmentGlintOverride?.also { value ->
            item.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, value)
        }

        attributeModifiers?.also { value ->
            val data = ItemAttributeModifiers.itemAttributes().apply {
                for (entry in value) {
                    addModifier(entry.attribute, entry.modifier)
                }
            }

            item.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, data)
        }
    }

    @SerialName("normal")
    @Serializable
    data class Normal(
        override val material: @Contextual Material,
        override val amount: Int? = null,
        override val name: String? = null,
        override val lore: List<String>? = null,
        override val tooltip: TooltipConfiguration? = null,
        override val enchantments: Map<@Contextual Enchantment, Int>? = null,
        override val customModelData: CustomModelDataConfiguration? = null,
        override val unbreakable: Boolean? = null,
        override val enchantmentGlintOverride: Boolean? = null,
        override val attributeModifiers: List<AttributeConfiguration>? = null,
        val trim: TrimConfiguration? = null,
    ) : ItemTemplate()

    @SerialName("armor")
    @Serializable
    data class Armor(
        override val material: @Contextual Material,
        override val amount: Int? = null,
        override val name: String? = null,
        override val lore: List<String>? = null,
        override val tooltip: TooltipConfiguration? = null,
        override val enchantments: Map<@Contextual Enchantment, Int>? = null,
        override val customModelData: CustomModelDataConfiguration? = null,
        override val unbreakable: Boolean? = null,
        override val enchantmentGlintOverride: Boolean? = null,
        override val attributeModifiers: List<AttributeConfiguration>? = null,
        val trim: TrimConfiguration? = null,
    ) : ItemTemplate() {

        override fun modifyItem(item: ItemStack, miniMessage: MiniMessage, tagResolver: TagResolver) {
            super.modifyItem(item, miniMessage, tagResolver)

            trim?.also {
                item.setData(
                    DataComponentTypes.TRIM,
                    ItemArmorTrim.itemArmorTrim(it.toArmorTrim()),
                )
            }
        }
    }

    @SerialName("player-head")
    @Serializable
    data class PlayerHead(
        val texture: String? = null,
        override val amount: Int? = null,
        override val name: String? = null,
        override val lore: List<String>? = null,
        override val tooltip: TooltipConfiguration? = null,
        override val enchantments: Map<@Contextual Enchantment, Int>? = null,
        override val customModelData: CustomModelDataConfiguration? = null,
        override val unbreakable: Boolean? = null,
        override val enchantmentGlintOverride: Boolean? = null,
        override val attributeModifiers: List<AttributeConfiguration>? = null,
    ) : ItemTemplate() {
        @Transient
        override val material = Material.PLAYER_HEAD

        override fun modifyItem(item: ItemStack, miniMessage: MiniMessage, tagResolver: TagResolver) {
            super.modifyItem(item, miniMessage, tagResolver)

            texture?.also { value ->
                item.setData(
                    DataComponentTypes.PROFILE,
                    ResolvableProfile.resolvableProfile().addProperty(ProfileProperty("textures", value))
                )
            }
        }
    }

    @SerialName("potion")
    @Serializable
    data class Potion(
        override val material: @Contextual Material,
        override val amount: Int? = null,
        override val name: String? = null,
        override val lore: List<String>? = null,
        override val tooltip: TooltipConfiguration? = null,
        override val enchantments: Map<@Contextual Enchantment, Int>? = null,
        override val customModelData: CustomModelDataConfiguration? = null,
        override val unbreakable: Boolean? = null,
        override val enchantmentGlintOverride: Boolean? = null,
        override val attributeModifiers: List<AttributeConfiguration>? = null,
        val type: @Contextual PotionType? = null,
        val color: @Contextual Color? = null,
        val effects: List<@Contextual PotionEffect>? = null,
    ) : ItemTemplate() {

        override fun modifyItem(item: ItemStack, miniMessage: MiniMessage, tagResolver: TagResolver) {
            super.modifyItem(item, miniMessage, tagResolver)

            val data = PotionContents.potionContents()
            type?.also { data.potion(type) }
            color?.also { data.customColor(color) }
            effects?.also { data.addCustomEffects(effects) }
            item.setData(DataComponentTypes.POTION_CONTENTS, data)
        }
    }
}
