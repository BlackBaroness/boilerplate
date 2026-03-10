## Hibernate session factory builder

That's the most complete and flexible builder ever!
Session factory creation is quite a complex process, Hibernate has a lot of steps to take to do so:

BootstrapServiceRegistryBuilder → StandardServiceRegistryBuilder → MetadataSources → MetadataBuilder →
SessionFactoryBuilder → SessionFactory

You usually don't really see these since you use some kind of constructor which skips some of these for you.

But now you have a nice way to do the full customization of that process under a nice DSL with direct access to wrapped
values!

It never presets any values for you. You get the exact thing you said it to.

Regardless of the order of you calls, it always combines all your calls with a true order.
You can do `standardServiceRegistry`, then `bootstrapServiceRegistry` **twice** and it will figure it out!

```kotlin
createSessionFactory {
    bootstrapServiceRegistry {
        classLoader = SomeClass::class.java.classLoader
    }

    standardServiceRegistry {
        statementBatchSize = 1000
        defaultBatchFetchSize = 1000
        queryPlanCacheMaxSize = 4096
        autocommit = false
        orderInserts = true
        orderUpdates = true
        inClauseParameterPadding = true
        keywordAutoQuoting = true
        hbm2ddlAuto = Action.UPDATE
        isolation = Connection.TRANSACTION_REPEATABLE_READ

        connectionProviderDisablesAutocommit = true
        connectionProvider = HikariCPConnectionProvider::class
        hikariMaxLifetime = 15.minutes
        hikariIdleTimeout = 10.minutes
        hikariPoolName = "my awesome pool"

        // a fast way to configure H2
        h2(
            directory = someDirectory,
            name = "databaseName",
            ignoreCase = null, // optional, H2 creation param
            caseInsensitiveIdentifiers = null // optional, H2 creation param
        )

        // some other databases has these too!
        mysql(MariaDbConfiguration)
        mariadb(MariaDbConfiguration)
        postgresql(PostgresConfiguration)
    }

    metadataSources {
        addAnnotatedClass(SomeClass::class)
    }

    metadata {
        addAttributeConverter(SomeConverter, true)
    }
}
```
