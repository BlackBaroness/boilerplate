@file:Suppress("VulnerableLibrariesLocal")

plugins {
    `kotlin-conventions-jvm21`
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
    api(project(":boilerplate-minecraft"))
    api(project(":boilerplate-adventure"))

    // Platforms
    compileOnly(libs.paper.latest)

    // Coroutines
    compileOnly(libs.kotlinx.coroutines)
    compileOnly(libs.mccoroutine.folia)

    // Adventure
    compileOnly(libs.adventure.core)
    compileOnly(libs.adventure.minimessage)
    compileOnly(libs.adventure.serializer.bungeecord)
    compileOnly(libs.adventure.serializer.gson)
    compileOnly(libs.adventure.serializer.legacy)
    compileOnly(libs.adventure.serializer.plain)
    compileOnly(libs.adventure.platform.bukkit)

    // Platform plugins
    compileOnly(libs.luckperms)
    compileOnly(libs.nbtapi)
    compileOnly(libs.placeholderapi)

    // InvUI
    compileOnly(libs.invui.core)
    compileOnly(libs.invui.kotlin)

    // LiteCommands
    compileOnly(libs.litecommands.core)
    compileOnly(libs.litecommands.framework)

    // Serialization
    compileOnly(libs.kotlinx.serialization.cbor)

    // Hibernate
    compileOnly(libs.hibernate.core)

    compileOnly(libs.guice.assistedinject)
    compileOnly(libs.kotlinx.serialization.json)
    compileOnly(libs.caffeine)
}
