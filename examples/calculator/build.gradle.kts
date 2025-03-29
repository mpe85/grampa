group = "com.github.mpe85"

plugins {
    kotlin("jvm") version "2.1.20"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.mpe85:grampa:1.5.0")
}

application {
    mainClass.set("com.github.mpe85.calculator.Main")
}
