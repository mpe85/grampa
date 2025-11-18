import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.2.21"
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
    id("org.jetbrains.dokka") version "2.1.0"
    id("org.jetbrains.kotlinx.kover") version "0.9.3"
    id("com.ncorti.ktfmt.gradle") version "0.25.0"
    id("com.vanniktech.maven.publish") version "0.35.0"
}

group = "com.github.mpe85"

version = "1.7.0-SNAPSHOT"

val baseJdk = 11
val baseVendor = JvmVendorSpec.ADOPTIUM
val baseLang = JavaLanguageVersion.of(baseJdk)
val baseTarget = JvmTarget.fromTarget(baseJdk.toString())

val gitUrl = "https://github.com/mpe85/${project.name}"
val gitScmUrl = "https://github.com/mpe85/${project.name}.git"
val testJdks = listOf(baseJdk, 17, 21, 25)

val toolchains = extensions.getByType(JavaToolchainService::class.java)
val baseTest = tasks.named<Test>("test")

repositories { mavenCentral() }

dependencies {
    // Kotlin BOM
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:2.2.21"))
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")

    // Other libs
    implementation("net.bytebuddy:byte-buddy:1.18.1")
    implementation("org.greenrobot:eventbus-java:3.3.1")
    implementation("com.ibm.icu:icu4j:78.1")

    // Kotest BOM
    testImplementation(platform("io.kotest:kotest-bom:6.0.5"))
    testImplementation("io.kotest:kotest-runner-junit5")
    testImplementation("io.kotest:kotest-assertions-core")
    testImplementation("io.kotest:kotest-property")

    // Other test libs
    testImplementation("io.mockk:mockk:1.14.6")
}

java {
    toolchain {
        languageVersion.set(baseLang)
        vendor.set(baseVendor)
    }
}

kotlin {
    explicitApi()
    jvmToolchain {
        languageVersion.set(baseLang)
        vendor.set(baseVendor)
    }
    compilerOptions {
        jvmTarget.set(baseTarget)
        freeCompilerArgs.add("-Xjdk-release=$baseJdk")
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
    withType<Test>().configureEach {
        useJUnitPlatform()
        testLogging { events("passed", "skipped", "failed") }
    }
    named<Test>("test") {
        javaLauncher.set(
            toolchains.launcherFor {
                languageVersion.set(baseLang)
                vendor.set(baseVendor)
            }
        )
    }
    named("check") { dependsOn(testJdks.filter { it != baseJdk }.map { "testOn$it" }) }
}

testJdks
    .filter { it != baseJdk }
    .forEach { v ->
        tasks.register<Test>("testOn$v") {
            group = JavaBasePlugin.VERIFICATION_GROUP
            description = "Runs unit tests on JDK $v"

            testClassesDirs = baseTest.get().testClassesDirs
            classpath = baseTest.get().classpath

            javaLauncher.set(
                toolchains.launcherFor {
                    languageVersion.set(JavaLanguageVersion.of(v))
                    vendor.set(baseVendor)
                }
            )
            shouldRunAfter(baseTest)
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
