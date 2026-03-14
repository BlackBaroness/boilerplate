@file:Suppress("VulnerableLibrariesLocal")

plugins {
    `kotlin-conventions-jvm17`
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

    // Coroutines
    compileOnly(libs.kotlinx.coroutines)

    // Serialization
    compileOnly(libs.kotlinx.serialization.json)

    // Dependency injection
    compileOnly(libs.guice.core)

    // Cache
    compileOnly(libs.caffeine)
}
