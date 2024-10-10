group = "com.github.mpe85"

plugins {
    kotlin("jvm") version "2.0.21"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.mpe85:grampa:1.4.0")
}

application {
    mainClass.set("com.github.mpe85.calculator.Main")
}
