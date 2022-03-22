object Versions {
    const val byteBuddy = "1.12.8"
    const val detekt = "1.19.0"
    const val dokka = "1.6.10"
    const val eventBus = "3.3.1"
    const val icu4j = "70.1"
    const val jacoco = "0.8.7"
    const val junit = "5.8.2"
    const val jvmTarget = "1.8"
    const val kotest = "5.2.1"
    const val kotlin = "1.6.10"
    const val ktlint = "0.45.1"
    const val ktlintPlugin = "10.2.1"
    const val mockk = "1.12.3"
    const val versions = "0.42.0"
}

object Plugins {
    const val detekt = "io.gitlab.arturbosch.detekt"
    const val dokka = "org.jetbrains.dokka"
    const val jacoco = "org.gradle.jacoco"
    const val kotlinJvm = "jvm"
    const val ktlint = "org.jlleitschuh.gradle.ktlint"
    const val mavenPublish = "org.gradle.maven-publish"
    const val signing = "org.gradle.signing"
    const val versions = "com.github.ben-manes.versions"
}

object Libs {
    const val byteBuddy = "net.bytebuddy:byte-buddy:${Versions.byteBuddy}"
    const val eventBus = "org.greenrobot:eventbus-java:${Versions.eventBus}"
    const val icu4j = "com.ibm.icu:icu4j:${Versions.icu4j}"
    const val junitJupiter = "org.junit.jupiter:junit-jupiter:${Versions.junit}"
    const val kotestAssertionsCore = "io.kotest:kotest-assertions-core:${Versions.kotest}"
    const val kotestProperty = "io.kotest:kotest-property:${Versions.kotest}"
    const val kotestRunnerJunit5 = "io.kotest:kotest-runner-junit5:${Versions.kotest}"
    const val kotlinReflect = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}"
    const val kotlinStdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
    const val mockk = "io.mockk:mockk:${Versions.mockk}"
}
