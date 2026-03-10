## HikariCP builder

Here is a simple HikariCP builder to make things easier. It has build-in presets for different databases.

```kotlin
val hikariConfig = HikariConfig().configure {
    readOnly()
    singleConnection()

    username = "user"
    password = "password"
    poolName = "mypool"
    leakDetectionThreshold = 10.seconds

    sqlite(someFile) // or mysql etc
}

val dataSource = HikariDataSource(hikariConfig)
```
