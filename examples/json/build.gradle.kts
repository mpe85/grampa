group = "com.github.mpe85"

plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

repositories { mavenCentral() }

dependencies { implementation(project(":")) }

application { mainClass.set("com.github.mpe85.json.Main") }
