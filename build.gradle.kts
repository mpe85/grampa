import com.github.benmanes.gradle.versions.updates.gradle.GradleReleaseChannel.CURRENT
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import java.util.Locale
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier

group = "com.github.mpe85"

version = "1.6.1-SNAPSHOT"

val additionalTestToolchains = listOf(8, 11, 21, 24)
val gitUrl = "https://github.com/mpe85/${project.name}"
val gitScmUrl = "https://github.com/mpe85/${project.name}.git"

plugins {
    kotlin("jvm") version "2.2.0"
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
    id("org.jetbrains.dokka") version "2.0.0"
    id("org.jetbrains.kotlinx.kover") version "0.9.1"
    id("com.ncorti.ktfmt.gradle") version "0.23.0"
    id("com.vanniktech.maven.publish") version "0.34.0"
    id("com.github.ben-manes.versions") version "0.52.0"
}

repositories { mavenCentral() }

dependencies {
    implementation("net.bytebuddy:byte-buddy:1.17.6")
    implementation("org.greenrobot:eventbus-java:3.3.1")
    implementation("com.ibm.icu:icu4j:77.1")
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.2.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.2.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.13.4")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testImplementation("io.kotest:kotest-property:5.9.1")
    testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testImplementation("io.mockk:mockk:1.14.5")
}

java { toolchain { languageVersion.set(JavaLanguageVersion.of(8)) } }

kotlin {
    explicitApi()
    jvmToolchain(8)
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

additionalTestToolchains.forEach {
    tasks.register<Test>("testOn$it") {
        group = JavaBasePlugin.VERIFICATION_GROUP
        javaLauncher.set(
            javaToolchains.launcherFor { languageVersion.set(JavaLanguageVersion.of(it)) }
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
        description.set("Grampa")
        url.set(gitUrl)
        licenses {
            license {
                name.set("MIT")
                url.set("$gitUrl/blob/main/LICENSE")
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
