## Nullable `lateinit`

```kotlin
var property by nullableLateinit<Type?>()
```

Unlike a normal `lateinit`, this variant allows you to use a nullable value as well.

It throws only if a value was never set.

---

## Wrapped `lateinit`

```kotlin
fun function() {
    var property by wrappedLateinit<Type>()
}
```

Unlike a normal `lateinit`, this variant allows you to use itself locally, inside a function.
