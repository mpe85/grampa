group = "com.github.mpe85"

plugins {
    kotlin("jvm") version "1.7.22"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8", "1.7.22"))
    implementation("com.github.mpe85:grampa:1.1.1")
}

application {
    mainClass.set("com.github.mpe85.calculator.Main")
}
