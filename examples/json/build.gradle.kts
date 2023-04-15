group = "com.github.mpe85"

plugins {
    kotlin("jvm") version "1.8.20"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8", "1.8.20"))
    implementation("com.github.mpe85:grampa:1.2.0")
}

application {
    mainClass.set("com.github.mpe85.json.Main")
}
