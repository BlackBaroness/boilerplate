package io.github.blackbaroness.boilerplate.adventure

import io.github.blackbaroness.boilerplate.Boilerplate
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentBuilder
import net.kyori.adventure.text.TextComponent
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType

val Boilerplate.Reflection.componentBuilder_build: MethodHandle by lazy {
    ComponentBuilder::class.java
        .getMethod("build")
        .let { MethodHandles.lookup().unreflect(it) }
}
