## Write-only properties

Write-only properties aren't and, likely, will never be supported in Kotlin. So that's a hack to have it.

```kotlin
var property by writeOnly<Type> {
    TODO("this is your setter, use $it here")
}
```

If a user tries to get the property, the property throws `NotImplementedError`.

It's a **questionable** solution, since it's not possible to reject read attempts at compile time. You should be careful
creating and using these.
