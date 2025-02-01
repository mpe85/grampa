group = "com.github.mpe85"

plugins {
    kotlin("jvm") version "2.1.10"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.mpe85:grampa:1.4.0")
}

application {
    mainClass.set("com.github.mpe85.json.Main")
}
