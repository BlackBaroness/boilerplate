plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "boilerplate"

include(
    "boilerplate-main",
    "boilerplate-minecraft",
    "boilerplate-adventure",
    "boilerplate-paper",
    "boilerplate-bungeecord",
)
