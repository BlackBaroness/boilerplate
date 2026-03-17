## Menu configuration

This documentation is only available in Russian since it targets my clients.

---

Пример конфига:
```yml
menu:
  title: '<red>title'

  structure:
  - '# # # # # # # # #'
  - '. . . . . . . . .'
  - '. . . x . y . . .'
  - '. . . . . . . . .'
  - '# # # # # # # # #'

  # Запрограммированные элементы:
  forced-elements:
    # Кнопка делает xyz, символ = x
    special-button: !<normal>
      material: 'stone'

    # Кнопка делает xyz, символ = y
    special-button-two: !<normal>
      material: 'stone'

  # Кастомные элементы:
  custom-elements:
    '#': !<normal>
      material: 'cyan_stained_glass_pane'
      tooltip:
        hide: true
```

Пример кода:
```kotlin
@Serializable
data class Config(
    val menu: MenuTemplate<Templates> = MenuTemplate(
        title = "<red>title".asMiniMessageComponent,
        structure = listOf(
            "# # # # # # # # #",
            ". . . . . . . . .",
            ". . . x . y . . .",
            ". . . . . . . . .",
            "# # # # # # # # #",
        ),
        templates = Templates(),
        customItems = mapOf(
            '#' to ItemTemplate.Normal(
                material = Material.CYAN_STAINED_GLASS_PANE,
                tooltip = ItemTemplate.TooltipConfiguration(hide = true)
            )
        )
    ),
)

@Serializable
data class Templates(
    @YamlComment("Кнопка делает xyz, символ = x")
    val specialButton: ItemTemplate = ItemTemplate.Normal(material = Material.STONE),

    @YamlComment("", "Кнопка делает xyz, символ = y")
    val specialButtonTwo: ItemTemplate = ItemTemplate.Normal(material = Material.STONE),
)

// Открытие:
plugin.open(player, config.menu) { menu, window, gui ->
    gui.ingredient('x') { SimpleItem(menu.forcedElements.specialButton.cachedItem) { click -> TODO() } }
    gui.ingredient('y') { SimpleItem(menu.forcedElements.specialButtonTwo.cachedItem) { click -> TODO() } }
}
```
