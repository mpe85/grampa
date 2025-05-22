rootProject.name = "grampa"

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":examples:calculator", ":examples:json")
