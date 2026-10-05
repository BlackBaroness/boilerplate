package io.github.blackbaroness.boilerplate.adventure

import io.github.blackbaroness.boilerplate.Boilerplate
import net.kyori.adventure.text.ComponentBuilder
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles

val Boilerplate.Reflection.componentBuilder_build: MethodHandle by lazy {
    ComponentBuilder::class.java
        .getMethod("build")
        .let { MethodHandles.lookup().unreflect(it) }
}
