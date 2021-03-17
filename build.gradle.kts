import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.benmanes.gradle.versions.updates.gradle.GradleReleaseChannel.CURRENT
import com.github.spotbugs.snom.SpotBugsExtension
import com.github.spotbugs.snom.SpotBugsTask
import org.gradle.api.plugins.BasePlugin.BUILD_GROUP
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import java.util.Date

plugins {
    id(Plugins.bintray) version Versions.bintray
    id(Plugins.detekt) version Versions.detekt
    id(Plugins.dokka) version Versions.dokka
    id(Plugins.jacoco)
    kotlin(Plugins.kotlinJvm) version Versions.kotlin
    id(Plugins.ktlint) version Versions.ktlintPlugin
    id(Plugins.mavenPublish)
    id(Plugins.spotbugs) version Versions.spotbugsPlugin
    id(Plugins.versions) version Versions.versions
}

group = "com.github.mpe85"
version = "0.9.3-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(Libs.byteBuddy)
    implementation(Libs.eventBus)
    implementation(Libs.icu4j)
    implementation(Libs.kotlinReflect)
    implementation(Libs.kotlinStdlib)
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
    artifacts {
        archives(javadocJar)
        archives(sourcesJar)
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
    version.set("0.41.0")
}

val gitUrl = "https://github.com/mpe85/${project.name}"
val gitScmUrl = "https://github.com/mpe85/${project.name}.git"

publishing {
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
            pom.withXml {
                asNode().apply {
                    appendNode("description", "Grampa")
                    appendNode("name", "$group:${project.name}")
                    appendNode("url", gitUrl)
                    appendNode("licenses").appendNode("license").apply {
                        appendNode("name", "MIT")
                        appendNode("url", "$gitUrl/blob/master/LICENSE")
                    }
                    appendNode("developers").appendNode("developer").apply {
                        appendNode("id", "mpe85")
                        appendNode("name", "Marco Perazzo")
                        appendNode("email", "marco.perazzo85@gmail.com")
                    }
                    appendNode("scm").apply {
                        appendNode("url", gitScmUrl)
                        appendNode("connection", "scm:git:$gitScmUrl")
                    }
                }
            }
        }
    }
}

val bintrayUser: String? by project
val bintrayKey: String? by project

bintray {
    user = bintrayUser ?: System.getenv("bintrayUser")
    key = bintrayKey ?: System.getenv("bintrayKey")
    setPublications("mavenJava")

    pkg.apply {
        repo = "maven"
        name = project.name
        setLicenses("MIT")
        websiteUrl = gitUrl
        vcsUrl = gitScmUrl
        issueTrackerUrl = "$gitUrl/issues"

        version.apply {
            name = "${project.version}"
            desc = "${project.version}"
            released = Date().toString()
            vcsTag = "${project.version}"
        }
    }
}

fun String.isNonStable(): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { toUpperCase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(this)
    return isStable.not()
}
