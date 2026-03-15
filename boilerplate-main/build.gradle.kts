@file:Suppress("VulnerableLibrariesLocal")

plugins {
    `kotlin-conventions-jvm17`
    `publish-conventions`
}

repositories {
    maven("https://repo.panda-lang.org/releases") // LiteCommands
}

dependencies {
    compileOnly(kotlin("reflect"))

    // Coroutines
    compileOnly(libs.kotlinx.coroutines)

    // Serialization
    compileOnly(libs.kotlinx.serialization.json)
    compileOnly(libs.kotlinx.serialization.cbor)
    compileOnly(libs.durationserializer)
    compileOnly(libs.kaml)

    // Hibernate
    compileOnly(libs.hibernate.core)
    compileOnly(libs.hibernate.hikaricp)

    // SQL
    compileOnly(libs.hikaricp)
    compileOnly(libs.h2)
    compileOnly(libs.mariadb)
    compileOnly(libs.postgresql)
    compileOnly(libs.mysql)
    compileOnly(libs.sqlite)

    // NoSQL
    compileOnly(libs.rocksdb)
    compileOnly(libs.redisson)

    // Dependency injection
    compileOnly(libs.guice.core)
    compileOnly(libs.guice.assistedinject)

    // Math
    compileOnly(libs.joml)

    // Reflection
    compileOnly(libs.bytebuddy)

    // Cache
    compileOnly(libs.caffeine)

    // Compression
    compileOnly(libs.zstd)

    compileOnly(libs.ipaddress)
}
