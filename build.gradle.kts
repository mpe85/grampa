import com.github.benmanes.gradle.versions.updates.gradle.GradleReleaseChannel.CURRENT
import org.gradle.api.plugins.BasePlugin.BUILD_GROUP
import java.util.Locale

group = "com.github.mpe85"
version = "1.5.0"
val additionalTestToolchains = listOf(11, 17, 21, 23)
val gitUrl = "https://github.com/mpe85/${project.name}"
val gitScmUrl = "https://github.com/mpe85/${project.name}.git"

plugins {
    kotlin("jvm") version "2.1.10"
    id("io.gitlab.arturbosch.detekt") version "1.23.7"
    id("org.jetbrains.dokka") version "2.0.0"
    id("org.jetbrains.kotlinx.kover") version "0.9.1"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.2"
    id("org.gradle.maven-publish")
    id("org.gradle.signing")
    id("com.github.ben-manes.versions") version "0.52.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.bytebuddy:byte-buddy:1.17.0")
    implementation("org.greenrobot:eventbus-java:3.3.1")
    implementation("com.ibm.icu:icu4j:76.1")
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.1.10")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.1.10")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testImplementation("io.kotest:kotest-property:5.9.1")
    testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testImplementation("io.mockk:mockk:1.13.16")
}

kotlin {
    explicitApi()
}

kover {
    reports {
        total {
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
}

additionalTestToolchains.forEach {
    tasks.register<Test>("testOn$it") {
        group = JavaBasePlugin.VERIFICATION_GROUP
        javaLauncher.set(
            javaToolchains.launcherFor {
                languageVersion.set(JavaLanguageVersion.of(it))
            },
        )
    }
}

val javadocJar by tasks.registering(Jar::class) {
    group = BUILD_GROUP
    dependsOn(tasks.dokkaGenerate)
    archiveClassifier.set("javadoc")
    from(tasks.dokkaGeneratePublicationHtml.get().outputDirectory)
}
val sourcesJar by tasks.registering(Jar::class) {
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
        checkConstraints = false
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
