@file:Suppress("VulnerableLibrariesLocal")

plugins {
    `kotlin-conventions`
    `publish-conventions`
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/") // Paper API
    maven("https://repo.codemc.io/repository/maven-public/") // NBT-API
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") // PlaceholderAPI
    maven("https://repo.xenondevs.xyz/releases") // InvUI
    maven("https://repo.panda-lang.org/releases") // LiteCommands
}

dependencies {
    api(project(":boilerplate-main"))

    // Adventure
    compileOnly(libs.adventure.core)
    compileOnly(libs.adventure.minimessage)
    compileOnly(libs.adventure.serializer.bungeecord)
    compileOnly(libs.adventure.serializer.gson)
    compileOnly(libs.adventure.serializer.legacy)
    compileOnly(libs.adventure.serializer.plain)

    // Hibernate
    compileOnly(libs.hibernate.core)

    // Serialization
    compileOnly(libs.durationserializer)

    // BungeeCord Chat support
    compileOnly(libs.bungeecordChat)
    // Serialization
    compileOnly(libs.kotlinx.serialization.json)
}
