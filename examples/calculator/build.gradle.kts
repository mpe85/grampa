group = "com.github.mpe85"

plugins {
    kotlin("jvm") version "2.4.10"
    application
}

repositories { mavenCentral() }

dependencies { implementation("com.github.mpe85:grampa:1.7.0") }

application { mainClass.set("com.github.mpe85.calculator.Main") }
