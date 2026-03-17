## Write-only properties

Write-only properties aren't and, likely, will never be supported in Kotlin. So that's a hack to have it.

```kotlin
var property by writeOnly<Type> {
    TODO("this is your setter, use $it here")
}
```

If a user tries to get the property, the property throws `NotImplementedError`.

To prevent calling get in compile time, you can do the following:

```kotlin
@get:Deprecated(message = WRITE_ONLY_MESSAGE, level = DeprecationLevel.ERROR)
var property by writeOnly<Type> {
    TODO("this is your setter, use $it here")
}
```
