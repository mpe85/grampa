group = "com.github.mpe85"

plugins {
    kotlin("jvm") version "2.2.10"
    application
}

repositories { mavenCentral() }

dependencies { implementation("com.github.mpe85:grampa:1.6.0") }

application { mainClass.set("com.github.mpe85.calculator.Main") }
