object Versions {
    const val BYTE_BUDDY = "1.14.11"
    const val DETEKT = "1.23.4"
    const val DOKKA = "1.9.10"
    const val EVENT_BUS = "3.3.1"
    const val ICU4J = "74.2"
    const val JUNIT = "5.10.1"
    const val KOTEST = "5.8.0"
    const val KOTLIN = "1.9.22"
    const val KOVER = "0.7.5"
    const val KTLINT = "1.1.1"
    const val KTLINT_PLUGIN = "12.1.0"
    const val MOCKK = "1.13.9"
    const val VERSIONS = "0.50.0"
}

object Plugins {
    const val DETEKT = "io.gitlab.arturbosch.detekt"
    const val DOKKA = "org.jetbrains.dokka"
    const val KOTLIN_JVM = "jvm"
    const val KOVER = "org.jetbrains.kotlinx.kover"
    const val KTLINT = "org.jlleitschuh.gradle.ktlint"
    const val MAVEN_PUBLISH = "org.gradle.maven-publish"
    const val SIGNING = "org.gradle.signing"
    const val VERSIONS = "com.github.ben-manes.versions"
}

object Libs {
    const val BYTE_BUDDY = "net.bytebuddy:byte-buddy:${Versions.BYTE_BUDDY}"
    const val EVENT_BUS = "org.greenrobot:eventbus-java:${Versions.EVENT_BUS}"
    const val ICU4J = "com.ibm.icu:icu4j:${Versions.ICU4J}"
    const val JUNIT_JUPITER = "org.junit.jupiter:junit-jupiter:${Versions.JUNIT}"
    const val KOTEST_ASSERTIONS_CORE = "io.kotest:kotest-assertions-core:${Versions.KOTEST}"
    const val KOTEST_PROPERTY = "io.kotest:kotest-property:${Versions.KOTEST}"
    const val KOTEST_RUNNER_JUNIT5 = "io.kotest:kotest-runner-junit5:${Versions.KOTEST}"
    const val KOTLIN_REFLECT = "org.jetbrains.kotlin:kotlin-reflect:${Versions.KOTLIN}"
    const val KOTLIN_STDLIB = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.KOTLIN}"
    const val MOCKK = "io.mockk:mockk:${Versions.MOCKK}"
}
