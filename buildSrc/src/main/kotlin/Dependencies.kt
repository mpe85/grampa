object Versions {
    const val byteBuddy = "1.11.13"
    const val detekt = "1.18.0"
    const val dokka = "1.5.0"
    const val eventBus = "3.2.0"
    const val icu4j = "69.1"
    const val jacoco = "0.8.7"
    const val junit = "5.7.2"
    const val jvmTarget = "1.8"
    const val kotest = "4.6.1"
    const val kotlin = "1.5.21"
    const val ktlint = "0.41.0"
    const val ktlintPlugin = "10.1.0"
    const val mockk = "1.12.0"
    const val versions = "0.39.0"
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
    const val eventBus = "org.greenrobot:eventbus:${Versions.eventBus}"
    const val icu4j = "com.ibm.icu:icu4j:${Versions.icu4j}"
    const val junitJupiter = "org.junit.jupiter:junit-jupiter:${Versions.junit}"
    const val kotestAssertionsCore = "io.kotest:kotest-assertions-core:${Versions.kotest}"
    const val kotestProperty = "io.kotest:kotest-property:${Versions.kotest}"
    const val kotestRunnerJunit5 = "io.kotest:kotest-runner-junit5:${Versions.kotest}"
    const val kotlinReflect = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}"
    const val kotlinStdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
    const val mockk = "io.mockk:mockk:${Versions.mockk}"
}
