@file:ApiStatus.Obsolete

package io.github.blackbaroness.boilerplate.paper.invui

import net.kyori.adventure.text.ComponentLike
import org.jetbrains.annotations.ApiStatus
import org.jetbrains.annotations.Contract
import xyz.xenondevs.invui.window.Window
import xyz.xenondevs.invui.window.type.context.setTitle

@Contract("_ -> this")
fun <B : Window.Builder<*, B>> B.setTitle(title: ComponentLike): B = setTitle(title.asComponent())
