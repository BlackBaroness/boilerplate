package io.github.blackbaroness.boilerplate.paper

import com.charleskorn.kaml.YamlComment
import io.github.blackbaroness.boilerplate.adventure.MiniMessageComponent
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class MenuTemplate<TEMPLATES>(
    val title: @Contextual MiniMessageComponent,

    @YamlComment("")
    val structure: List<String>,

    @YamlComment("", "Запрограммированные элементы:")
    val forcedElements: TEMPLATES,

    @YamlComment("", "Кастомные элементы:")
    val customElements: Map<Char, ItemTemplate> = mapOf(),
)
