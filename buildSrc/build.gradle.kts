import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(plugin(libs.plugins.kotlin.jvm))
    implementation(plugin(libs.plugins.kotlin.plugin.serialization))
}

kotlin {
    jvmToolchain(21)
}

tasks.withType<JavaCompile> {
    options.release = 21
}

tasks.withType<KotlinCompile> {
    compilerOptions.jvmTarget = JvmTarget.JVM_21
}

fun plugin(plugin: Provider<PluginDependency>): Provider<String> =
    plugin.map { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" }
