import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.21"
    application
}

group = "com.github.mpe85"

repositories {
    mavenCentral()
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    implementation(kotlin("stdlib-jdk8", "1.6.21"))
    implementation("com.github.mpe85:grampa:0.9.6-SNAPSHOT")
}

tasks.withType<JavaCompile> {
    targetCompatibility = "1.8"
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("com.github.mpe85.json.Main")
}
