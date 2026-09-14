import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import com.vanniktech.maven.publish.SourcesJar
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.detekt)
    alias(libs.plugins.dokka)
    alias(libs.plugins.kover)
    alias(libs.plugins.ktfmt)
    alias(libs.plugins.maven.publish)
}

group = "com.github.mpe85"

version = "1.7.1-SNAPSHOT"

val baseJdk = 11
val baseVendor: JvmVendorSpec = JvmVendorSpec.ADOPTIUM
val baseLang: JavaLanguageVersion = JavaLanguageVersion.of(baseJdk)
val baseTarget = JvmTarget.fromTarget(baseJdk.toString())

val gitUrl = "https://github.com/mpe85/${project.name}"
val gitScmUrl = "https://github.com/mpe85/${project.name}.git"
val testJdks = listOf(baseJdk, 17, 21, 25, 26)

val toolchains: JavaToolchainService = extensions.getByType(JavaToolchainService::class.java)
val baseTest = tasks.named<Test>("test")

repositories { mavenCentral() }

dependencies {
    // Kotlin BOM & Libraries
    implementation(platform(libs.kotlin.bom))
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.stdlib)

    // Other libs
    implementation(libs.bytebuddy)
    implementation(libs.eventbus)
    implementation(libs.icu4j)

    // Kotest BOM & Bundle
    testImplementation(platform(libs.kotest.bom))
    testImplementation(libs.bundles.kotest)

    // Other test libs
    testImplementation(libs.mockk)
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
        KotlinJvm(
            javadocJar = JavadocJar.Dokka("dokkaGeneratePublicationHtml"),
            sourcesJar = SourcesJar.Sources(),
        )
    )
}
