group = "com.github.mpe85"

plugins {
    kotlin("jvm") version "1.7.10"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8", "1.7.10"))
    implementation("com.github.mpe85:grampa:1.0.0")
}

application {
    mainClass.set("com.github.mpe85.calculator.Main")
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}
