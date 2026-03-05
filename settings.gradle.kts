plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "boilerplate"

include(
    "boilerplate-all",
    "boilerplate-main",
    "boilerplate-minecraft",
    "boilerplate-adventure",
    "boilerplate-paper",
)
