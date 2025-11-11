import com.github.benmanes.gradle.versions.updates.gradle.GradleReleaseChannel.CURRENT
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import java.util.Locale
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

group = "com.github.mpe85"

version = "1.7.0-SNAPSHOT"

val additionalTestToolchains = listOf(17, 21, 25)
val gitUrl = "https://github.com/mpe85/${project.name}"
val gitScmUrl = "https://github.com/mpe85/${project.name}.git"

plugins {
    kotlin("jvm") version "2.2.21"
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
    id("org.jetbrains.dokka") version "2.1.0"
    id("org.jetbrains.kotlinx.kover") version "0.9.3"
    id("com.ncorti.ktfmt.gradle") version "0.25.0"
    id("com.vanniktech.maven.publish") version "0.35.0"
    id("com.github.ben-manes.versions") version "0.53.0"
}

repositories { mavenCentral() }

dependencies {
    // Kotlin BOM
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:2.2.21"))
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")

    // Other libs
    implementation("net.bytebuddy:byte-buddy:1.18.0")
    implementation("org.greenrobot:eventbus-java:3.3.1")
    implementation("com.ibm.icu:icu4j:78.1")

    // JUnit BOM
    testImplementation(platform("org.junit:junit-bom:5.14.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // Kotest BOM
    testImplementation(platform("io.kotest:kotest-bom:6.0.4"))
    testImplementation("io.kotest:kotest-runner-junit5")
    testImplementation("io.kotest:kotest-assertions-core")
    testImplementation("io.kotest:kotest-property")

    // Other test libs
    testImplementation("io.mockk:mockk:1.14.6")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
        vendor.set(JvmVendorSpec.ADOPTIUM)
    }
}

kotlin {
    explicitApi()
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
        vendor.set(JvmVendorSpec.ADOPTIUM)
    }
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
        freeCompilerArgs.add("-Xjdk-release=11")
    }
}

kover {
    reports {
        total {
            html { onCheck = true }
            xml { onCheck = true }
            verify { onCheck = true }
        }
    }
}

dokka {
    dokkaSourceSets.configureEach {
        documentedVisibilities(VisibilityModifier.Public, VisibilityModifier.Protected)
    }
}

ktfmt { kotlinLangStyle() }

val baseTest = tasks.named<Test>("test")

additionalTestToolchains.forEach {
    tasks.register<Test>("testOn$it") {
        dependsOn(baseTest)
        group = JavaBasePlugin.VERIFICATION_GROUP
        testClassesDirs = baseTest.get().testClassesDirs
        classpath = baseTest.get().classpath

        javaLauncher.set(
            javaToolchains.launcherFor {
                languageVersion.set(JavaLanguageVersion.of(it))
                vendor.set(JvmVendorSpec.ADOPTIUM)
            }
        )
    }
}

tasks {
    jar {
        manifest {
            attributes["Implementation-Title"] = project.name
            attributes["Implementation-Version"] = project.version
            attributes["Implementation-Vendor"] = project.group
            attributes["Created-By"] =
                "${System.getProperty("java.version")} (${System.getProperty("java.vendor")})"
            attributes["Automatic-Module-Name"] = "${project.group}.${project.name}"
        }
    }
    dependencyUpdates {
        revision = "release"
        checkConstraints = false
        gradleReleaseChannel = CURRENT.id
        rejectVersionIf { candidate.version.isNonStable() }
    }
    withType<Test>().configureEach {
        useJUnitPlatform()
        testLogging { events("passed", "skipped", "failed") }
    }
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    coordinates("$group", project.name, "$version")

    pom {
        name.set("Grampa")
        description.set("A PEG parser library for Kotlin/JVM")
        inceptionYear.set("2022")
        url.set(gitUrl)
        licenses {
            license {
                name.set("The MIT License")
                url.set("https://opensource.org/licenses/MIT")
                distribution.set("https://opensource.org/licenses/MIT")
            }
        }
        developers {
            developer {
                id.set("mpe85")
                name.set("Marco Perazzo")
                email.set("marco.perazzo85@gmail.com")
            }
        }
        scm {
            connection.set(gitScmUrl)
            developerConnection.set(gitScmUrl)
            url.set(gitUrl)
        }
    }

    configure(
        KotlinJvm(javadocJar = JavadocJar.Dokka("dokkaGeneratePublicationHtml"), sourcesJar = true)
    )
}

fun String.isNonStable(): Boolean {
    val stableKeyword =
        listOf("RELEASE", "FINAL", "GA").any { uppercase(Locale.ENGLISH).contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(this)
    return isStable.not()
}
