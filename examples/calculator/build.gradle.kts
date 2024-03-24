group = "com.github.mpe85"

plugins {
    kotlin("jvm") version "1.9.23"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.mpe85:grampa:1.3.0")
}

application {
    mainClass.set("com.github.mpe85.calculator.Main")
}
