import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("kotlin-conventions")
}

kotlin {
    jvmToolchain(21)

    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

tasks.withType<JavaCompile>() {
    options.release.set(17)
}

configurations.matching { it.isCanBeResolved }.configureEach {
    attributes {
        attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 21)
    }
}

configurations.matching { it.isCanBeConsumed }.configureEach {
    attributes {
        attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 17)
    }
}
