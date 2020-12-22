import com.github.spotbugs.snom.SpotBugsExtension
import com.github.spotbugs.snom.SpotBugsTask
import java.util.Date
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
  repositories {
    jcenter()
  }
  dependencies {
  }
}

plugins {
  id(Plugins.bintray) version Versions.bintray
  id(Plugins.detekt) version Versions.detekt
  id(Plugins.dokka) version Versions.dokka
  kotlin(Plugins.kotlinJvm) version Versions.kotlin
  id(Plugins.mavenPublish)
  id(Plugins.versions) version Versions.versions
  jacoco
  maven

  id("com.github.spotbugs") version "4.6.0"
}

group = "com.mpe85"
version = "0.9.3-SNAPSHOT"

repositories {
  jcenter()
  mavenCentral()
}

dependencies {
  implementation(Libs.byteBuddy)
  implementation(Libs.eventBus)
  implementation(Libs.icu4j)
  implementation(Libs.kassava)
  implementation(Libs.kotlinReflect)
  implementation(Libs.kotlinStdlib)
  compileOnly("com.github.spotbugs:spotbugs-annotations:4.2.0")
  testImplementation(Libs.kotestAssertionsCore)
  testImplementation(Libs.kotestRunnerJunit5)
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
  testImplementation("org.junit.jupiter:junit-jupiter-params:5.7.0")
  testImplementation("org.mockito:mockito-junit-jupiter:3.6.28")
  testImplementation(kotlin("test-junit5"))
  testImplementation("io.mockk:mockk:1.10.3")
  testCompileOnly("com.github.spotbugs:spotbugs-annotations:4.2.0")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

val javadocJar = tasks.create<Jar>("javadocJar") {
  dependsOn("javadoc")
  archiveClassifier.set("javadoc")
  from(tasks.getByName<Javadoc>("javadoc").destinationDir)
}
val sourcesJar = tasks.create<Jar>("sourcesJar") {
  dependsOn("classes")
  archiveClassifier.set("sources")
  from(sourceSets.main.get().allSource)
}

tasks {
  withType<JavaCompile> {
    options.encoding = "UTF-8"
  }
  withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
  }
  withType<Javadoc> {
    (options as? StandardJavadocDocletOptions)?.links("https://docs.oracle.com/en/java/javase/11/docs/api/")
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
}

configure<SpotBugsExtension> {
  setEffort("max")
  setReportLevel("low")
  ignoreFailures.set(true)
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
