group = "com.github.mpe85"

plugins {
    kotlin("jvm") version "1.9.23"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8", "1.9.23"))
    implementation("com.github.mpe85:grampa:1.3.0")
}

application {
    mainClass.set("com.github.mpe85.json.Main")
}
