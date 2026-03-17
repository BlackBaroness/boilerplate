@file:Suppress("VulnerableLibrariesLocal")

plugins {
    `kotlin-conventions-jvm17`
    `publish-conventions`
}

repositories {
    mavenCentral()
    maven("https://libraries.minecraft.net/")
}

dependencies {
    api(project(":boilerplate-main"))
    api(project(":boilerplate-minecraft"))
    api(project(":boilerplate-adventure"))

    // Platforms
    compileOnly(libs.bungeecord)

    // Coroutines
    compileOnly(libs.kotlinx.coroutines)
    compileOnly(libs.mccoroutine.bungeecord)

    // Adventure
    compileOnly(libs.adventure.platform.bungeecord)

    // Serialization
    compileOnly(libs.kotlinx.serialization.cbor)

    compileOnly(libs.bytebuddy)
}
