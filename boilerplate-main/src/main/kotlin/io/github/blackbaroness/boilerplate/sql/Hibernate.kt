package io.github.blackbaroness.boilerplate.sql

import io.github.blackbaroness.boilerplate.writeOnly
import jakarta.persistence.AttributeConverter
import org.hibernate.*
import org.hibernate.boot.Metadata
import org.hibernate.boot.MetadataBuilder
import org.hibernate.boot.MetadataSources
import org.hibernate.boot.registry.BootstrapServiceRegistry
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder
import org.hibernate.boot.registry.StandardServiceRegistry
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.Environment
import org.hibernate.cfg.HikariCPSettings
import org.hibernate.dialect.Dialect
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider
import org.hibernate.engine.spi.SessionFactoryImplementor
import org.hibernate.event.service.spi.EventListenerRegistry
import org.hibernate.event.spi.EventType
import org.hibernate.query.Query
import org.hibernate.tool.schema.Action
import java.io.File
import java.nio.file.Path
import java.sql.Driver
import java.util.stream.Stream
import kotlin.io.path.absolutePathString
import kotlin.reflect.KClass
import kotlin.time.Duration

inline fun <reified T> Session.fetchAll(): Stream<T> {
    val query = this.criteriaBuilder.createQuery(T::class.java)
    return createQuery(query.select(query.from(T::class.java))).resultStream
}

inline fun <reified T : Any> SharedSessionContract.createQuery(hql: String): Query<T> {
    return createQuery(hql, T::class.java)
}

// A copy of SessionFactory#inTransaction, but with an inline action
inline fun <T> SessionFactory.inTransactionInline(action: (Session) -> T): T {
    return openSession().use { session ->
        val transaction = session.beginTransaction()

        try {
            val result = action.invoke(session)

            if (!transaction.isActive)
                throw TransactionManagementException("Execution of action caused managed transaction to be completed")

            transaction.commit()

            result
        } catch (exception: Exception) {
            // an error happened in the action or during commit()
            if (transaction.isActive) {
                try {
                    transaction.rollback()
                } catch (e: java.lang.RuntimeException) {
                    exception.addSuppressed(e)
                }
            }
            throw exception
        }
    }
}

// A copy of SessionFactory#fromStatelessTransaction, but with an inline action
inline fun <T> SessionFactory.inStatelessTransactionInline(action: (StatelessSession) -> T): T {
    return openStatelessSession().use { session ->
        val transaction = session.beginTransaction()

        try {
            val result = action.invoke(session)

            if (!transaction.isActive)
                throw TransactionManagementException("Execution of action caused managed transaction to be completed")

            transaction.commit()

            result
        } catch (exception: Exception) {
            // an error happened in the action or during commit()
            if (transaction.isActive) {
                try {
                    transaction.rollback()
                } catch (e: RuntimeException) {
                    exception.addSuppressed(e)
                }
            }
            throw exception
        }
    }
}

fun <T> SessionFactory.registerListener(type: EventType<T>, listener: T) {
    unwrap(SessionFactoryImplementor::class.java)
        .serviceRegistry
        .getService(EventListenerRegistry::class.java)!!
        .appendListeners(type, listener)
}

@DslMarker
annotation class HibernateDsl

fun createSessionFactory(action: SessionFactoryBuilder.() -> Unit): SessionFactory {
    return SessionFactoryBuilder().apply(action).build()
}

@HibernateDsl
class SessionFactoryBuilder internal constructor() {

    private val bootstrapRegistry = mutableListOf<BootstrapRegistryConfigurator.() -> Unit>()
    private val standardRegistry = mutableListOf<StandardRegistryConfigurator.() -> Unit>()
    private val metadataSources = mutableListOf<MetadataSourcesConfigurator.() -> Unit>()
    private val metadata = mutableListOf<MetadataConfigurator.() -> Unit>()
    private val sessionFactory = mutableListOf<SessionFactoryConfigurator.() -> Unit>()

    fun bootstrapServiceRegistry(action: (BootstrapRegistryConfigurator.() -> Unit)) {
        bootstrapRegistry += action
    }

    fun standardServiceRegistry(action: (StandardRegistryConfigurator.() -> Unit)) {
        standardRegistry += action
    }

    fun metadataSources(action: (MetadataSourcesConfigurator.() -> Unit)) {
        metadataSources += action
    }

    fun metadata(action: (MetadataConfigurator.() -> Unit)) {
        metadata += action
    }

    fun sessionFactory(action: (SessionFactoryConfigurator.() -> Unit)) {
        sessionFactory += action
    }

    internal fun build(): SessionFactory {
        val boostrapRegistryConfigurator = BootstrapRegistryConfigurator()
        bootstrapRegistry.forEach { it.invoke(boostrapRegistryConfigurator) }
        val boostrapRegistry = boostrapRegistryConfigurator.build()

        val standardRegistryConfigurator = StandardRegistryConfigurator(boostrapRegistry)
        standardRegistry.forEach { it.invoke(standardRegistryConfigurator) }
        val standardRegistry = standardRegistryConfigurator.build()

        val metadataSourcesConfigurator = MetadataSourcesConfigurator(standardRegistry)
        metadataSources.forEach { it.invoke(metadataSourcesConfigurator) }
        val metadataSources = metadataSourcesConfigurator.build()

        val metadataConfigurator = MetadataConfigurator(metadataSources)
        metadata.forEach { it.invoke(metadataConfigurator) }
        val metadata = metadataConfigurator.build()

        val sessionFactoryConfigurator = SessionFactoryConfigurator(metadata)
        sessionFactory.forEach { it.invoke(sessionFactoryConfigurator) }
        return sessionFactoryConfigurator.build()
    }
}

@HibernateDsl
class BootstrapRegistryConfigurator {

    val wrapped = BootstrapServiceRegistryBuilder()

    var classLoader by writeOnly<ClassLoader> {
        wrapped.applyClassLoader(it)
    }

    internal fun build(): BootstrapServiceRegistry = wrapped.build()
}

@HibernateDsl
class StandardRegistryConfigurator internal constructor(registry: BootstrapServiceRegistry) {

    val wrapped = StandardServiceRegistryBuilder(registry)

    var jdbcUrl by writeOnly<String> {
        wrapped.applySetting(Environment.JAKARTA_JDBC_URL, it)
    }

    var jdbcUser by writeOnly<String> {
        wrapped.applySetting(Environment.JAKARTA_JDBC_USER, it)
    }

    var jdbcPassword by writeOnly<String> {
        wrapped.applySetting(Environment.JAKARTA_JDBC_PASSWORD, it)
    }

    var jdbcDriver by writeOnly<KClass<out Driver>> {
        wrapped.applySetting(Environment.JAKARTA_JDBC_DRIVER, it.java.name)
    }

    var dialect by writeOnly<KClass<out Dialect>> {
        wrapped.applySetting(Environment.DIALECT, it.java.name)
    }

    var connectionProvider by writeOnly<KClass<out ConnectionProvider>> {
        wrapped.applySetting(Environment.CONNECTION_PROVIDER, it.java.name)
    }

    var hbm2ddlAuto by writeOnly<Action> {
        wrapped.applySetting(Environment.HBM2DDL_AUTO, it.externalHbm2ddlName)
    }

    var statementBatchSize by writeOnly<Int> {
        wrapped.applySetting(Environment.STATEMENT_BATCH_SIZE, it)
    }

    var defaultBatchFetchSize by writeOnly<Int> {
        wrapped.applySetting(Environment.DEFAULT_BATCH_FETCH_SIZE, it)
    }

    var orderInserts by writeOnly<Boolean> {
        wrapped.applySetting(Environment.ORDER_INSERTS, it)
    }

    var orderUpdates by writeOnly<Boolean> {
        wrapped.applySetting(Environment.ORDER_UPDATES, it)
    }

    var inClauseParameterPadding by writeOnly<Boolean> {
        wrapped.applySetting(Environment.IN_CLAUSE_PARAMETER_PADDING, it)
    }

    var queryPlanCacheMaxSize by writeOnly<Int> {
        wrapped.applySetting(Environment.QUERY_PLAN_CACHE_MAX_SIZE, it)
    }

    var autocommit by writeOnly<Boolean> {
        wrapped.applySetting(Environment.AUTOCOMMIT, it)
    }

    var connectionProviderDisablesAutocommit by writeOnly<Boolean> {
        wrapped.applySetting(Environment.CONNECTION_PROVIDER_DISABLES_AUTOCOMMIT, it)
    }

    var keywordAutoQuoting by writeOnly<Boolean> {
        wrapped.applySetting(Environment.KEYWORD_AUTO_QUOTING_ENABLED, it)
    }

    var globallyQuotedIdentifiers by writeOnly<Boolean> {
        wrapped.applySetting(Environment.GLOBALLY_QUOTED_IDENTIFIERS, it)
    }

    var isolation by writeOnly<Int> {
        wrapped.applySetting(Environment.ISOLATION, it)
    }

    var hikariMaxLifetime by writeOnly<Duration> {
        wrapped.applySetting(HikariCPSettings.HIKARI_MAX_LIFETIME, it.inWholeMilliseconds)
    }

    var hikariIdleTimeout by writeOnly<Duration> {
        wrapped.applySetting(HikariCPSettings.HIKARI_IDLE_TIMEOUT, it.inWholeMilliseconds)
    }

    var hikariPoolName by writeOnly<String> {
        wrapped.applySetting(HikariCPSettings.HIKARI_POOL_NAME, it)
    }

    fun h2(
        directory: Path,
        name: String,
        ignoreCase: Boolean? = null,
        caseInsensitiveIdentifiers: Boolean? = null,
    ) {
        jdbcUser = "sa"
        jdbcPassword = ""
        jdbcDriver = org.h2.Driver::class
        jdbcUrl = buildString {
            append("jdbc:h2:")
            append(directory.absolutePathString())
            append(File.separator)
            append(name)

            if (ignoreCase != null) {
                append(";IGNORECASE=")
                append(ignoreCase.toString().uppercase())
            }

            if (caseInsensitiveIdentifiers != null) {
                append(";CASE_INSENSITIVE_IDENTIFIERS=")
                append(caseInsensitiveIdentifiers.toString().uppercase())
            }
        }
    }

    fun mysql(config: MariaDbConfiguration) {
        jdbcUser = config.user
        jdbcPassword = config.password
        jdbcUrl = buildString {
            append("jdbc:mysql://")
            append(config.address)
            append(':')
            append(config.port)
            append('/')
            append(config.database)
            append(config.parameters.joinToString(prefix = "?", separator = "&"))
        }
        jdbcDriver = com.mysql.cj.jdbc.Driver::class

        // required since it doesn't register itself for some reason
        com.mysql.cj.jdbc.Driver()
    }

    fun mariadb(config: MariaDbConfiguration) {
        jdbcUser = config.user
        jdbcPassword = config.password
        jdbcUrl = buildString {
            append("jdbc:mariadb://")
            append(config.address)
            append(':')
            append(config.port)
            append('/')
            append(config.database)
            append(config.parameters.joinToString(prefix = "?", separator = "&"))
        }
        jdbcDriver = org.mariadb.jdbc.Driver::class
    }

    fun postgresql(config: PostgresConfiguration) {
        jdbcUser = config.user
        jdbcPassword = config.password
        jdbcUrl = buildString {
            append("jdbc:postgresql://")
            append(config.address)
            append(':')
            append(config.port)
            append('/')
            append(config.database)
            append(config.parameters.joinToString(prefix = "?", separator = "&"))
        }
        jdbcDriver = org.postgresql.Driver::class
    }

    internal fun build(): StandardServiceRegistry = wrapped.build()
}

@HibernateDsl
class MetadataSourcesConfigurator internal constructor(registry: StandardServiceRegistry) {

    val wrapped = MetadataSources(registry)

    fun addAnnotatedClass(clazz: KClass<*>) {
        wrapped.addAnnotatedClass(clazz.java)
    }

    internal fun build(): MetadataSources = wrapped
}

@HibernateDsl
class MetadataConfigurator internal constructor(metadataSources: MetadataSources) {

    val wrapped: MetadataBuilder = metadataSources.metadataBuilder

    fun addAttributeConverter(converter: AttributeConverter<*, *>) {
        wrapped.applyAttributeConverter(converter)
    }

    fun addAttributeConverter(converter: AttributeConverter<*, *>, autoApply: Boolean) {
        wrapped.applyAttributeConverter(converter, autoApply)
    }

    internal fun build(): Metadata = wrapped.build()
}

@HibernateDsl
class SessionFactoryConfigurator internal constructor(metadata: Metadata) {

    val wrapped: org.hibernate.boot.SessionFactoryBuilder = metadata.sessionFactoryBuilder

    internal fun build(): SessionFactory = wrapped.build()
}
