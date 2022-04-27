group = "com.github.mpe85"

plugins {
    kotlin("jvm") version "1.6.21"
    application
}

repositories {
    mavenCentral()
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    implementation(kotlin("stdlib-jdk8", "1.6.21"))
    implementation("com.github.mpe85:grampa:0.9.6-SNAPSHOT")
}

application {
    mainClass.set("com.github.mpe85.calculator.Main")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
