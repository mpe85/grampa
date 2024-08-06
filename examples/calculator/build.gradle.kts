group = "com.github.mpe85"

plugins {
    kotlin("jvm") version "2.0.10"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.mpe85:grampa:1.3.1")
}

application {
    mainClass.set("com.github.mpe85.calculator.Main")
}
