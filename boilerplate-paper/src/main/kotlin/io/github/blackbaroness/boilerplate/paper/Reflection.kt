package io.github.blackbaroness.boilerplate.paper

import io.github.blackbaroness.boilerplate.Boilerplate
import org.bukkit.Material
import org.bukkit.inventory.meta.ItemMeta
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles

val Boilerplate.Reflection.material_getDefaultAttributeModifiers: MethodHandle? by lazy {
    Material::class.java.methods
        .firstOrNull { it.name == "getDefaultAttributeModifiers" && it.parameterCount == 0 }
        ?.let { MethodHandles.lookup().unreflect(it) }
        ?.also { loggerSafe.info("Detected Material.getDefaultAttributeModifiers()") }
}

val Boilerplate.Reflection.itemMeta_setAttributeModifiers: MethodHandle? by lazy {
    ItemMeta::class.java.methods
        .firstOrNull { it.name == "setAttributeModifiers" && it.parameterCount == 1 }
        ?.let { MethodHandles.lookup().unreflect(it) }
        ?.also { loggerSafe.info("Detected ItemMeta.setAttributeModifiers()") }
}
