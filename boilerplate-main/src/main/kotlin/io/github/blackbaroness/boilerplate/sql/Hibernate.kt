package io.github.blackbaroness.boilerplate.sql

import io.github.blackbaroness.boilerplate.Boilerplate
import io.github.blackbaroness.boilerplate.WRITE_ONLY_MESSAGE
import io.github.blackbaroness.boilerplate.copyAndClose
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
import org.hibernate.cache.jcache.ConfigSettings
import org.hibernate.cache.jcache.MissingCacheStrategy
import org.hibernate.cache.spi.RegionFactory
import org.hibernate.cfg.Environment
import org.hibernate.cfg.HikariCPSettings
import org.hibernate.dialect.Dialect
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider
import org.hibernate.engine.spi.SessionFactoryImplementor
import org.hibernate.event.service.spi.EventListenerRegistry
import org.hibernate.event.spi.EventType
import org.hibernate.proxy.HibernateProxy
import org.hibernate.query.Query
import org.hibernate.tool.schema.Action
import java.io.File
import java.io.InputStream
import java.net.URI
import java.nio.file.Path
import java.sql.Driver
import java.util.*
import java.util.stream.Stream
import javax.cache.spi.CachingProvider
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectories
import kotlin.io.path.createTempFile
import kotlin.io.path.outputStream
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

inline fun <reified T : Any> Boilerplate.hibernateEquals(obj: T, other: Any?, equals: (T) -> Boolean): Boolean {
    if (obj === other) return true
    if (other == null) return false

    val thisEffectiveClass = if (obj is HibernateProxy) {
        obj.hibernateLazyInitializer.persistentClass
    } else {
        obj.javaClass
    }

    val oEffectiveClass = if (other is HibernateProxy) {
        other.hibernateLazyInitializer.persistentClass
    } else {
        other.javaClass
    }

    if (thisEffectiveClass != oEffectiveClass) return false
    return equals.invoke(other as T)
}

@Suppress("UnusedReceiverParameter")
fun Boilerplate.hibernateHashCode(obj: Any, id: Any?): Int {
    return if (id != null) Objects.hash(id) else System.identityHashCode(obj)
}

@Suppress("UnusedReceiverParameter")
fun Boilerplate.hibernateHashCode(vararg ids: Any): Int {
    return Objects.hash(*ids)
}

@DslMarker
annotation class HibernateDsl

fun Boilerplate.createSessionFactory(action: SessionFactoryBuilder.() -> Unit): SessionFactory {
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

    @get:Deprecated(message = "This property is write-only", level = DeprecationLevel.ERROR)
    var classLoader by writeOnly<ClassLoader> {
        wrapped.applyClassLoader(it)
    }

    internal fun build(): BootstrapServiceRegistry = wrapped.build()
}

@HibernateDsl
class StandardRegistryConfigurator internal constructor(registry: BootstrapServiceRegistry) {

    val wrapped = StandardServiceRegistryBuilder(registry)

    @get:Deprecated(message = WRITE_ONLY_MESSAGE, level = DeprecationLevel.ERROR)
    var jdbcUrl by writeOnly<String> {
        wrapped.applySetting(Environment.JAKARTA_JDBC_URL, it)
    }

    @get:Deprecated(message = WRITE_ONLY_MESSAGE, level = DeprecationLevel.ERROR)
    var jdbcUser by writeOnly<String> {
        wrapped.applySetting(Environment.JAKARTA_JDBC_USER, it)
    }

    @get:Deprecated(message = WRITE_ONLY_MESSAGE, level = DeprecationLevel.ERROR)
    var jdbcPassword by writeOnly<String> {
        wrapped.applySetting(Environment.JAKARTA_JDBC_PASSWORD, it)
    }

    @get:Deprecated(message = WRITE_ONLY_MESSAGE, level = DeprecationLevel.ERROR)
    var jdbcDriver by writeOnly<KClass<out Driver>> {
        wrapped.applySetting(Environment.JAKARTA_JDBC_DRIVER, it.java.name)
    }

    @get:Deprecated(message = WRITE_ONLY_MESSAGE, level = DeprecationLevel.ERROR)
    var dialect by writeOnly<KClass<out Dialect>> {
        wrapped.applySetting(Environment.DIALECT, it.java)
    }

    @get:Deprecated(message = WRITE_ONLY_MESSAGE, level = DeprecationLevel.ERROR)
    var connectionProvider by writeOnly<KClass<out ConnectionProvider>> {
        wrapped.applySetting(Environment.CONNECTION_PROVIDER, it.java)
    }

    @get:Deprecated(message = WRITE_ONLY_MESSAGE, level = DeprecationLevel.ERROR)
    var hbm2ddlAuto by writeOnly<Action> {
        wrapped.applySetting(Environment.HBM2DDL_AUTO, it.externalHbm2ddlName)
    }

    @get:Deprecated(message = WRITE_ONLY_MESSAGE, level = DeprecationLevel.ERROR)
    var statementBatchSize by writeOnly<Int> {
        wrapped.applySetting(Environment.STATEMENT_BATCH_SIZE, it)
    }

    @get:Deprecated(message = WRITE_ONLY_MESSAGE, level = DeprecationLevel.ERROR)
    var defaultBatchFetchSize by writeOnly<Int> {
        wrapped.applySetting(Environment.DEFAULT_BATCH_FETCH_SIZE, it)
    }

    @get:Deprecated(message = WRITE_ONLY_MESSAGE, level = DeprecationLevel.ERROR)
    var orderInserts by writeOnly<Boolean> {
        wrapped.applySetting(Environment.ORDER_INSERTS, it)
    }

    @get:Deprecated(message = WRITE_ONLY_MESSAGE, level = DeprecationLevel.ERROR)
    var orderUpdates by writeOnly<Boolean> {
        wrapped.applySetting(Environment.ORDER_UPDATES, it)
    }

    @get:Deprecated(message = WRITE_ONLY_MESSAGE, level = DeprecationLevel.ERROR)
    var inClauseParameterPadding by writeOnly<Boolean> {
        wrapped.applySetting(Environment.IN_CLAUSE_PARAMETER_PADDING, it)
    }

    @get:Deprecated(message = WRITE_ONLY_MESSAGE, level = DeprecationLevel.ERROR)
    var queryPlanCacheMaxSize by writeOnly<Int> {
        wrapped.applySetting(Environment.QUERY_PLAN_CACHE_MAX_SIZE, it)
    }

    @get:Deprecated(message = WRITE_ONLY_MESSAGE, level = DeprecationLevel.ERROR)
    var autocommit by writeOnly<Boolean> {
        wrapped.applySetting(Environment.AUTOCOMMIT, it)
    }

    @get:Deprecated(message = WRITE_ONLY_MESSAGE, level = DeprecationLevel.ERROR)
    var connectionProviderDisablesAutocommit by writeOnly<Boolean> {
        wrapped.applySetting(Environment.CONNECTION_PROVIDER_DISABLES_AUTOCOMMIT, it)
    }

    @get:Deprecated(message = WRITE_ONLY_MESSAGE, level = DeprecationLevel.ERROR)
    var keywordAutoQuoting by writeOnly<Boolean> {
        wrapped.applySetting(Environment.KEYWORD_AUTO_QUOTING_ENABLED, it)
    }

    @get:Deprecated(message = WRITE_ONLY_MESSAGE, level = DeprecationLevel.ERROR)
    var globallyQuotedIdentifiers by writeOnly<Boolean> {
        wrapped.applySetting(Environment.GLOBALLY_QUOTED_IDENTIFIERS, it)
    }

    @get:Deprecated(message = WRITE_ONLY_MESSAGE, level = DeprecationLevel.ERROR)
    var isolation by writeOnly<Int> {
        wrapped.applySetting(Environment.ISOLATION, it)
    }

    @get:Deprecated(message = WRITE_ONLY_MESSAGE, level = DeprecationLevel.ERROR)
    var useSecondLevelCache by writeOnly<Boolean> {
        wrapped.applySetting(Environment.USE_SECOND_LEVEL_CACHE, it)
    }

    @get:Deprecated(message = WRITE_ONLY_MESSAGE, level = DeprecationLevel.ERROR)
    var cacheRegionFactory by writeOnly<KClass<out RegionFactory>> {
        wrapped.applySetting(Environment.CACHE_REGION_FACTORY, it.java)
    }

    @get:Deprecated(message = WRITE_ONLY_MESSAGE, level = DeprecationLevel.ERROR)
    var hikariMaxLifetime by writeOnly<Duration> {
        wrapped.applySetting(HikariCPSettings.HIKARI_MAX_LIFETIME, it.inWholeMilliseconds)
    }

    @get:Deprecated(message = WRITE_ONLY_MESSAGE, level = DeprecationLevel.ERROR)
    var hikariIdleTimeout by writeOnly<Duration> {
        wrapped.applySetting(HikariCPSettings.HIKARI_IDLE_TIMEOUT, it.inWholeMilliseconds)
    }

    @get:Deprecated(message = WRITE_ONLY_MESSAGE, level = DeprecationLevel.ERROR)
    var hikariPoolName by writeOnly<String> {
        wrapped.applySetting(HikariCPSettings.HIKARI_POOL_NAME, it)
    }

    @get:Deprecated(message = WRITE_ONLY_MESSAGE, level = DeprecationLevel.ERROR)
    var jcacheProvider by writeOnly<KClass<out CachingProvider>> {
        wrapped.applySetting(ConfigSettings.PROVIDER, it.java.name)
    }

    @get:Deprecated(message = WRITE_ONLY_MESSAGE, level = DeprecationLevel.ERROR)
    var jcacheMissingCacheStrategy by writeOnly<MissingCacheStrategy> {
        wrapped.applySetting(ConfigSettings.MISSING_CACHE_STRATEGY, it.externalRepresentation)
    }

    @get:Deprecated(message = WRITE_ONLY_MESSAGE, level = DeprecationLevel.ERROR)
    var jcacheConfigUri by writeOnly<URI> {
        wrapped.applySetting(ConfigSettings.CONFIG_URI, it.toString())
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

    fun attachJCacheConfigFile(text: String, temporaryDir: Path? = null, deleteOnExit: Boolean = true) {
        attachJCacheConfigFile(
            inputStream = text.byteInputStream(),
            temporaryDir = temporaryDir,
            deleteOnExit = deleteOnExit
        )
    }

    fun attachJCacheConfigFile(inputStream: InputStream, temporaryDir: Path? = null, deleteOnExit: Boolean = true) {
        temporaryDir?.createDirectories()
        val file = createTempFile(prefix = "jcache.conf", directory = temporaryDir)

        if (deleteOnExit) {
            file.toFile().deleteOnExit()
        }

        copyAndClose(inputStream, file.outputStream())
        attachJCacheConfigFile(file)
    }

    fun attachJCacheConfigFile(file: Path) {
        jcacheConfigUri = file.toUri()
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
