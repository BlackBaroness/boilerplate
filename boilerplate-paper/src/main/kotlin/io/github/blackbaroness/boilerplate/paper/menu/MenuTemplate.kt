package io.github.blackbaroness.boilerplate.paper.menu

import com.charleskorn.kaml.YamlComment
import io.github.blackbaroness.boilerplate.adventure.MiniMessageComponent
import io.github.blackbaroness.boilerplate.paper.item.ItemStackProvider
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class MenuTemplate<TEMPLATES, CUSTOM_ELEMENTS_PROVIDER : ItemStackProvider>(
    val title: @Contextual MiniMessageComponent,

    @YamlComment("")
    val structure: List<String>,

    @YamlComment("", "Запрограммированные элементы:")
    val forcedElements: TEMPLATES,

    @YamlComment("", "Кастомные элементы:")
    val customElements: Map<Char, CUSTOM_ELEMENTS_PROVIDER> = mapOf(),
)
