import com.github.benmanes.gradle.versions.updates.gradle.GradleReleaseChannel.CURRENT
import org.gradle.api.plugins.BasePlugin.BUILD_GROUP
import org.jetbrains.dokka.gradle.DokkaTask
import java.util.Locale

group = "com.github.mpe85"
version = "1.3.1-SNAPSHOT"
val additionalTestToolchains = listOf(11, 17, 21, 22)
val gitUrl = "https://github.com/mpe85/${project.name}"
val gitScmUrl = "https://github.com/mpe85/${project.name}.git"

plugins {
    id(Plugins.DETEKT) version Versions.DETEKT
    id(Plugins.DOKKA) version Versions.DOKKA
    kotlin(Plugins.KOTLIN_JVM) version Versions.KOTLIN
    id(Plugins.KOVER) version Versions.KOVER
    id(Plugins.KTLINT) version Versions.KTLINT_PLUGIN
    id(Plugins.MAVEN_PUBLISH)
    id(Plugins.SIGNING)
    id(Plugins.VERSIONS) version Versions.VERSIONS
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(Libs.BYTE_BUDDY)
    implementation(Libs.EVENT_BUS)
    implementation(Libs.ICU4J)
    implementation(Libs.KOTLIN_REFLECT)
    implementation(Libs.KOTLIN_STDLIB)
    testImplementation(Libs.JUNIT_JUPITER)
    testImplementation(Libs.KOTEST_ASSERTIONS_CORE)
    testImplementation(Libs.KOTEST_PROPERTY)
    testImplementation(Libs.KOTEST_RUNNER_JUNIT5)
    testImplementation(Libs.MOCKK)
}

kotlin {
    explicitApi()
}

ktlint {
    version.set(Versions.KTLINT)
}

koverReport {
    defaults {
        html {
            onCheck = true
        }
        xml {
            onCheck = true
        }
        verify {
            onCheck = true
        }
    }
}

additionalTestToolchains.forEach {
    tasks.create<Test>("testOn$it") {
        group = JavaBasePlugin.VERIFICATION_GROUP
        javaLauncher.set(
            javaToolchains.launcherFor {
                languageVersion.set(JavaLanguageVersion.of(it))
            },
        )
    }
}

val javadocJar by tasks.creating(Jar::class) {
    group = BUILD_GROUP
    dependsOn("dokkaHtml")
    archiveClassifier.set("javadoc")
    from(tasks.getByName<DokkaTask>("dokkaHtml").outputDirectory)
}
val sourcesJar by tasks.creating(Jar::class) {
    group = BUILD_GROUP
    dependsOn("classes")
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

artifacts {
    archives(javadocJar)
    archives(sourcesJar)
}

tasks {
    jar {
        manifest {
            attributes["Implementation-Title"] = project.name
            attributes["Implementation-Version"] = project.version
            attributes["Implementation-Vendor"] = project.group
            attributes["Created-By"] = "${System.getProperty("java.version")} (${System.getProperty("java.vendor")})"
            attributes["Automatic-Module-Name"] = "${project.group}.${project.name}"
        }
    }
    dependencyUpdates {
        revision = "release"
        gradleReleaseChannel = CURRENT.id
        rejectVersionIf {
            candidate.version.isNonStable()
        }
    }
    withType<Test>().configureEach {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}

publishing {
    repositories {
        maven {
            val releasesRepoUrl = "https://s01.oss.sonatype.org/content/repositories/releases/"
            val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
            credentials {
                username = "ossrhUsername".property()
                password = "ossrhPassword".property()
            }
        }
    }
    publications {
        register<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(sourcesJar) {
                classifier = "sources"
            }
            artifact(javadocJar) {
                classifier = "javadoc"
            }
            groupId = "$group"
            artifactId = project.name
            version = "${project.version}"

            pom {
                name.set("Grampa")
                description.set("Grampa")
                url.set(gitUrl)
                licenses {
                    license {
                        name.set("MIT")
                        url.set("$gitUrl/blob/master/LICENSE")
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
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}

fun String.isNonStable(): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { uppercase(Locale.ENGLISH).contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(this)
    return isStable.not()
}

fun String.property() = if (hasProperty(this)) property(this)?.toString() else null
