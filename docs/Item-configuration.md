## Item configuration

This documentation is only available in Russian since it targets my clients.

---

Предметы бывают разных типов:

- `normal` - просто обычные
- `player-head` - головы игроков (с текстурами)
- `potion` - зелья

`normal` покрывает основные параметры любого предмета - название, зачарования и прочее.
Остальные типы имеют то же самое, только плюсом ещё и свои уникальные параметры.

Тип указывается через !<id>, это называется [YAML тэгом](https://yaml.org/spec/1.2/spec.html#id2761292).

Например,

```yml
секция: !<normal>
  # а тут уже параметры
```

Как правило, все поля необязательны - их можно удалить или установить `null` вместо значения.

Везде, где принимается текст, используется [MiniMessage](https://docs.papermc.io/adventure/minimessage/format/).

## `normal` - минимальный пример

```yml
секция: !<normal>
    material: 'cobblestone' # id предмета
```

## `normal` - полный пример

```yml
секция: !<normal>
  material: 'cobblestone' # id предмета

  # Количество:
  amount: 2

  # Название:
  name: '<aqua>обычный предмет'

  # Лор (описание):
  lore:
    - '<gray>строка 1'
    - '<red>строка 2'

  # Настройки подсказки при наведении на предмет:
  tooltip:
    # true полностью скрывает подсказку:
    hide: true
    # Скрытие отдельных компонентов:
    hidden-components:
      - 'enchantments'

    # Зачарования, id к уровню:
  enchantments:
    'aqua_affinity': 6

  # Custom model data, нужно для использования текстур из ресурспака:
  custom-model-data:
    floats:
      - 1.5
    flags:
      - true
      - false
    strings:
      - 'abc'
    colors:
      - '#00FFFF'

  # true делает предмет бесконечно прочным:
  unbreakable: false

  # true заставляет предмет переливаться, даже без зачарований:
  enchantment-glint-override: true

  # Список атрибутов:
  attribute-modifiers:
    - attribute: 'armor'
      modifier:
      id: 'e019d54e-742d-424b-995b-b7cd028a5707'
      amount: 1.0
      operation: 'ADD_NUMBER'
      slot-group: 'body'
```

## `player-head` - пример

```yml
секция: !<player-head>
  # Текстура это base64 "value" с сайтов по типу https://minecraft-heads.com/
  texture: 'eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmYzMDYwMjRiNjRiNTg3ZGNhZjYwN2UyM2EyMGVhMmUzMmNkMzFlY2QzYmMyNzU3ZmY1MGYxM2RiYmFkMzFiMSJ9fX0='

  # + поддерживаются все параметры из normal, кроме material
  # (material) устанавливается автоматически
```

## `potion` - пример

```yml
секция: !<potion>
  # id предмета, как и в normal:
  material: 'splash_potion'

  # Тип зелья:
  type: 'long_fire_resistance'

  # Цвет зелья:
  color: '#00FFFF'

  # Кастомные эффекты зелья:
  effects:
    - type: 'glowing'
      duration: 60
      amplifier: 1
    - type: 'hunger'
      duration: 100
      amplifier: 3
      ambient: false
      particles: false
      icon: false

  # + поддерживаются все параметры из normal
```
