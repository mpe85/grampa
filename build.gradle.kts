import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.benmanes.gradle.versions.updates.gradle.GradleReleaseChannel.CURRENT
import com.github.spotbugs.snom.SpotBugsExtension
import com.github.spotbugs.snom.SpotBugsTask
import org.gradle.api.plugins.BasePlugin.BUILD_GROUP
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.KtlintExtension

plugins {
    id(Plugins.detekt) version Versions.detekt
    id(Plugins.dokka) version Versions.dokka
    id(Plugins.jacoco)
    kotlin(Plugins.kotlinJvm) version Versions.kotlin
    id(Plugins.ktlint) version Versions.ktlintPlugin
    id(Plugins.mavenPublish)
    id(Plugins.signing)
    id(Plugins.spotbugs) version Versions.spotbugsPlugin
    id(Plugins.versions) version Versions.versions
}

group = "com.github.mpe85"
version = "0.9.3-SNAPSHOT"

repositories {
    maven("https://kotlin.bintray.com/kotlinx") // until kotlinx.html is migrated to mavenCentral
    mavenCentral()
}

dependencies {
    implementation(Libs.byteBuddy)
    implementation(Libs.eventBus)
    implementation(Libs.icu4j)
    implementation(Libs.kotlinReflect)
    implementation(Libs.kotlinStdlib)
    testImplementation(Libs.junitJupiter)
    testImplementation(Libs.kotestAssertionsCore)
    testImplementation(Libs.kotestProperty)
    testImplementation(Libs.kotestRunnerJunit5)
    testImplementation(Libs.mockk)
    testCompileOnly(Libs.spotbugsAnnotations)
}

kotlin {
    explicitApi()
}

jacoco {
    toolVersion = Versions.jacoco
}

spotbugs {
    toolVersion.set(Versions.spotbugs)
}

val javadocJar = tasks.create<Jar>("javadocJar") {
    group = BUILD_GROUP
    dependsOn("dokkaHtml")
    archiveClassifier.set("javadoc")
    from(tasks.getByName<DokkaTask>("dokkaHtml").outputDirectory)
}
val sourcesJar = tasks.create<Jar>("sourcesJar") {
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
    named<SpotBugsTask>("spotbugsTest") {
        enabled = false
    }
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = Versions.jvmTarget
    }
    withType<Jar> {
        manifest {
            attributes["Implementation-Title"] = project.name
            attributes["Implementation-Version"] = project.version
            attributes["Implementation-Vendor"] = project.group
            attributes["Created-By"] = "${System.getProperty("java.version")} (${System.getProperty("java.vendor")})"
            attributes["Automatic-Module-Name"] = "${project.group}.${project.name}"
        }
    }
    withType<JacocoReport> {
        reports {
            xml.isEnabled = true
        }
    }
    withType<Test> {
        useJUnitPlatform()
        dependsOn("cleanTest")
        testLogging {
            events("passed", "skipped", "failed")
        }
        finalizedBy("jacocoTestReport")
    }
    withType<SpotBugsTask> {
        reports {
            create("html") {
                isEnabled = true
            }
            create("xml") {
                isEnabled = false
            }
        }
    }
    withType<DependencyUpdatesTask> {
        revision = "release"
        gradleReleaseChannel = CURRENT.id
        rejectVersionIf {
            candidate.version.isNonStable()
        }
    }
}

configure<SpotBugsExtension> {
    setEffort("max")
    setReportLevel("low")
    ignoreFailures.set(true)
}

configure<KtlintExtension> {
    version.set(Versions.ktlint)
}

val gitUrl = "https://github.com/mpe85/${project.name}"
val gitScmUrl = "https://github.com/mpe85/${project.name}.git"

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
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { toUpperCase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(this)
    return isStable.not()
}

fun String.property() = if (hasProperty(this)) property(this)?.toString() else null
