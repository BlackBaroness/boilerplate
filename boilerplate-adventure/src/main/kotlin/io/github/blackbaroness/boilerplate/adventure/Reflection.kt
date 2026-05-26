package io.github.blackbaroness.boilerplate.adventure

import io.github.blackbaroness.boilerplate.Boilerplate
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType

val Boilerplate.Reflection.textComponentBuilder_build: MethodHandle by lazy {
    MethodHandles.lookup().findVirtual(
        TextComponent.Builder::class.java,
        "build",
        MethodType.methodType(Component::class.java)
    )
}
