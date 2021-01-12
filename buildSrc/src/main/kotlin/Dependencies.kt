object Versions {
    const val bintray = "1.8.5"
    const val byteBuddy = "1.10.19"
    const val detekt = "1.15.0"
    const val dokka = "1.4.20"
    const val eventBus = "3.2.0"
    const val jacoco = "0.8.6"
    const val icu4j = "68.2"
    const val kotest = "4.3.2"
    const val kotlin = "1.4.21"
    const val mockk = "1.10.4"
    const val spotbugs = "4.2.0"
    const val spotbugsPlugin = "4.6.0"
    const val versions = "0.36.0"
}

object Plugins {
    const val bintray = "com.jfrog.bintray"
    const val detekt = "io.gitlab.arturbosch.detekt"
    const val dokka = "org.jetbrains.dokka"
    const val jacoco = "org.gradle.jacoco"
    const val kotlinJvm = "jvm"
    const val mavenPublish = "org.gradle.maven-publish"
    const val spotbugs = "com.github.spotbugs"
    const val versions = "com.github.ben-manes.versions"
}

object Libs {
    const val byteBuddy = "net.bytebuddy:byte-buddy:${Versions.byteBuddy}"
    const val eventBus = "org.greenrobot:eventbus:${Versions.eventBus}"
    const val icu4j = "com.ibm.icu:icu4j:${Versions.icu4j}"
    const val kotestAssertionsCore = "io.kotest:kotest-assertions-core:${Versions.kotest}"
    const val kotestProperty = "io.kotest:kotest-property:${Versions.kotest}"
    const val kotestRunnerJunit5 = "io.kotest:kotest-runner-junit5:${Versions.kotest}"
    const val kotlinReflect = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}"
    const val kotlinStdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
    const val mockk = "io.mockk:mockk:${Versions.mockk}"
    const val spotbugsAnnotations = "com.github.spotbugs:spotbugs-annotations:${Versions.spotbugs}"
}
