@file:Suppress("removal")

package io.github.blackbaroness.boilerplate.paper.model

import com.destroystokyo.paper.profile.ProfileProperty
import io.github.blackbaroness.boilerplate.Boilerplate
import io.github.blackbaroness.boilerplate.adventure.asBungeeCordComponents
import io.github.blackbaroness.boilerplate.adventure.parseMiniMessage
import io.github.blackbaroness.boilerplate.paper.asBukkitColor
import io.github.blackbaroness.boilerplate.paper.isNativeAdventureApiAvailable
import io.github.blackbaroness.boilerplate.paper.itemMeta_setAttributeModifiers
import io.github.blackbaroness.boilerplate.paper.material_getDefaultAttributeModifiers
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.FireworkEffect
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.*
import org.bukkit.potion.PotionData
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionType
import org.jetbrains.annotations.ApiStatus
import xyz.xenondevs.invui.item.ItemProvider
import java.awt.Color
import java.util.*

@ApiStatus.Obsolete
@Serializable
data class ItemTemplate(
    val material: @Contextual Material,
    val amount: Int? = null,
    val displayName: String? = null,
    val lore: List<String>? = null,
    val customModelData: Int? = null,
    val flags: Set<ItemFlag>? = null,
    val enchantments: Map<@Contextual Enchantment, Int>? = null,
    val unbreakable: Boolean? = null,
    val potion: @Contextual PotionTemplate? = null,
    val firework: @Contextual FireworkTemplate? = null,
    val headTexture: String? = null,
    val storedEnchantments: Map<@Contextual Enchantment, Int>? = null,
    val attributes: List<AttributeConfiguration>? = null,
    val leatherArmorColor: @Contextual Color? = null,
) : ItemProvider {

    @Serializable
    data class PotionTemplate(
        val type: PotionType,
        val extended: Boolean,
        val upgraded: Boolean,
        val color: @Contextual Color? = null,
        val effects: List<@Contextual PotionEffect>? = null,
    )

    @Serializable
    data class FireworkTemplate(
        val colors: List<@Contextual Color>,
        val fadeColors: List<@Contextual Color>? = null,
        val flicker: Boolean = false,
        val trail: Boolean = false,
        val type: FireworkEffect.Type = FireworkEffect.Type.BALL,
    )

    val unsafeItem by lazy { resolve() }

    val safeItem get() = unsafeItem.clone()

    @Suppress("DEPRECATION", "removal")
    fun resolve(vararg tagResolvers: TagResolver): ItemStack {
        val resolver = TagResolver.resolver(*tagResolvers)

        val item = ItemStack(material, amount ?: 1)
        val meta = item.itemMeta ?: throw IllegalStateException("itemMeta is null for '$this'")

        if (displayName != null) {
            if (Boilerplate.isNativeAdventureApiAvailable) {
                meta.displayName(displayName.parseMiniMessage(resolver))
            } else {
                meta.setDisplayNameComponent(displayName.parseMiniMessage(resolver).asBungeeCordComponents)
            }
        }

        if (lore != null) {
            if (Boilerplate.isNativeAdventureApiAvailable) {
                meta.lore(lore.map { it.parseMiniMessage(resolver) })
            } else {
                meta.loreComponents = lore.map { it.parseMiniMessage(resolver).asBungeeCordComponents }
            }
        }

        if (customModelData != null) {
            meta.setCustomModelData(customModelData)
        }

        if (flags != null) {
            Boilerplate.Reflection.material_getDefaultAttributeModifiers?.let { getModifiers ->
                Boilerplate.Reflection.itemMeta_setAttributeModifiers?.invoke(meta, getModifiers.invoke(material))
            }

            meta.addItemFlags(*flags.toTypedArray())
        }

        enchantments?.forEach { (enchantment, level) ->
            meta.addEnchant(enchantment, level, true)
        }

        if (unbreakable != null) {
            meta.isUnbreakable = unbreakable
        }

        if (potion != null && meta is PotionMeta) {
            meta.basePotionData = PotionData(
                potion.type,
                potion.extended,
                potion.extended
            )

            if (potion.color != null) {
                meta.color = potion.color.asBukkitColor
            }

            potion.effects?.forEach { potionEffect ->
                meta.addCustomEffect(potionEffect, true)
            }
        }

        if (headTexture != null && meta is SkullMeta) {
            val uuid = UUID.nameUUIDFromBytes("Skull:$headTexture".encodeToByteArray())
            val profile = Bukkit.createProfile(uuid)
            profile.setProperty(ProfileProperty("textures", headTexture))
            meta.playerProfile = profile
        }

        if (storedEnchantments != null && meta is EnchantmentStorageMeta) {
            storedEnchantments.forEach { (enchantment, level) ->
                meta.addStoredEnchant(enchantment, level, true)
            }
        }

        attributes?.forEach { attribute ->
            meta.addAttributeModifier(attribute.attribute, attribute.modifier)
        }

        if (leatherArmorColor != null && meta is LeatherArmorMeta) {
            meta.setColor(leatherArmorColor.asBukkitColor)
        }

        if (firework != null) {
            val effect = FireworkEffect.builder().run {
                withColor(firework.colors.map { it.asBukkitColor })
                with(firework.type)
                firework.fadeColors?.let { withFade(it.map { color -> color.asBukkitColor }) }
                if (firework.flicker) flicker(true)
                if (firework.trail) trail(true)
                build()
            }

            when (meta) {
                is FireworkMeta -> meta.addEffect(effect)
                is FireworkEffectMeta -> meta.effect = effect
            }
        }

        item.itemMeta = meta
        return item
    }

    override fun get(lang: String?): ItemStack = unsafeItem
}
