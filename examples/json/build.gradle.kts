group = "com.github.mpe85"

plugins {
    kotlin("jvm") version "2.3.0"
    application
}

repositories { mavenCentral() }

dependencies { implementation("com.github.mpe85:grampa:1.6.1") }

application { mainClass.set("com.github.mpe85.json.Main") }
