@file:Suppress("VulnerableLibrariesLocal")

plugins {
    `kotlin-conventions`
    `publish-conventions`
}

repositories {
    maven("https://repo.panda-lang.org/releases") // LiteCommands
}

dependencies {
    // Coroutines
    compileOnly(libs.kotlinx.coroutines)

    // Serialization
    compileOnly(libs.kotlinx.serialization.json)
    compileOnly(libs.kotlinx.serialization.cbor)
    compileOnly(libs.durationserializer)

    // Hibernate
    compileOnly(libs.hibernate.core)
    compileOnly(libs.hibernate.hikaricp)

    // SQL
    compileOnly(libs.h2)
    compileOnly(libs.mariadb)
    compileOnly(libs.postgresql)

    // NoSQL
    compileOnly(libs.rocksdb)
    compileOnly(libs.redisson)

    // Dependency injection
    compileOnly(libs.guice.core)
    compileOnly(libs.guice.assistedinject)

    // Adventure
    compileOnly(libs.adventure.core)
    compileOnly(libs.adventure.minimessage)
    compileOnly(libs.adventure.serializer.bungeecord)
    compileOnly(libs.adventure.serializer.gson)
    compileOnly(libs.adventure.serializer.legacy)
    compileOnly(libs.adventure.serializer.plain)
    compileOnly(libs.adventure.platform.bukkit)
    compileOnly(libs.adventure.platform.bungeecord)

    // Math
    compileOnly(libs.joml)

    // Reflection
    compileOnly(libs.bytebuddy)

    // Cache
    compileOnly(libs.caffeine)

    // Compression
    compileOnly(libs.zstd)
}
