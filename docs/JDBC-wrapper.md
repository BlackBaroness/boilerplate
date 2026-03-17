## JDBC wrapper

A clean, zero overhead wrapper for JDBC queries. Write simple queries in a more Kotlin way.

```kotlin
val dataSource: DataSource

dataSource.connect { // it grabbed a connection from a source
    // using "first" --- it calls next(), grabs a value and closes the ResultSet afterward
    val count = query("SELECT COUNT(*) FROM `mytable`;").first {
        it.getLong(1)
    }

    // nullable variant
    val countNullable = query("SELECT COUNT(*) FROM `mytable`;").firstOrNull {
        it.getLong(1)
    }

    // using "forEach" --- it calls next() until no rows left and closes the ResultSet afterward
    query("SELECT FROM mytable WHERE id > 100").forEach { rs ->
        println(rs.getLong("id"))
    }

    // using "map" --- it calls next() until no rows left, maps rows into collection and closes the ResultSet afterward
    val ids = query("SELECT FROM mytable WHERE id > 100").map { rs ->
        rs.getLong("id")
    }

    // iterate all entries using an automatic keyset pagination
    forEachPaged("mytable", "id", pageSize = 500) { rs ->
        println(rs.getLong("id"))
    }
}
```
