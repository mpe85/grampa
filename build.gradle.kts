buildscript {
    repositories {
        jcenter()
    }
    dependencies {
    }
}

plugins {
    java
    jacoco
    maven
    `maven-publish`
    id("com.diffplug.gradle.spotless") version "3.27.0"
    id("com.jfrog.bintray") version "1.8.4"
    //id("com.github.spotbugs") version "3.0.0"
    id("com.github.ben-manes.versions") version "0.27.0"
}

group = "com.mpe85"

repositories {
    jcenter()
}

configurations {
    testCompileOnly.get().extendsFrom(configurations.compileOnly.get())
}

dependencies {
    implementation("com.google.guava:guava:28.2-jre")
    implementation("net.bytebuddy:byte-buddy:1.10.6")
    implementation("com.ibm.icu:icu4j:65.1")
    compileOnly("com.github.spotbugs:spotbugs-annotations:3.1.12")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.5.2")
    testImplementation("org.mockito:mockito-junit-jupiter:3.2.4")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.5.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.5.2")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
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
    val javadocJar = create<Jar>("javadocJar") {
        dependsOn("javadoc")
        archiveClassifier.set("javadoc")
        from(this@tasks.getByName<Javadoc>("javadoc").destinationDir)
    }
    val sourcesJar = create<Jar>("sourcesJar") {
        dependsOn("classes")
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }
    artifacts {
        archives(javadocJar)
        archives(sourcesJar)
    }
    /*
    withType<SpotBugsTask> {
        reports {
            html.isEnabled = true
            xml.isEnabled = false
        }
    }*/
}
/*
configure<SpotBugsExtension> {
    toolVersion = "3.1.12"
    effort = "max"
    reportLevel = "low"
    isIgnoreFailures = true
}
*/
/*

spotless {
    java {
        eclipse().configFile '.settings/org.eclipse.jdt.core.prefs'
    }
}

def gitUrl = "https://github.com/mpe85/${project.name}"

def pomConfig = {
    licenses {
        license {
            name 'MIT'
            url "${gitUrl}/blob/master/LICENSE"
        }
    }
    developers {
        developer {
            id 'mpe85'
            name 'Marco Perazzo'
            email 'marco.perazzo85@gmail.com'
        }
    }
    scm {
        url "${gitUrl}"
    }
}

publishing {
    publications {
        mavenJava(MavenPublication) {
            from components . java
                    artifact sourcesJar {
                classifier "sources"
            }
            artifact javadocJar {
                classifier "javadoc"
            }
            groupId "${group}"
            artifactId "${project.name}"
            version "$project.version"
            pom.withXml {
                def root = asNode ()
                root.appendNode('description', 'A sample project')
                root.appendNode('name', "${group}:${project.name}")
                root.appendNode('url', "${gitUrl}")
                root.children().last() + pomConfig
            }
        }
    }
}

bintray {
    user = project.hasProperty('bintrayUser') ? bintrayUser : System.env.bintrayUser
    key = project.hasProperty('bintrayKey') ? bintrayKey : System.env.bintrayKey
    publications = ['mavenJava']

    pkg {
        repo = 'maven'
        name = "${project.name}"
        licenses = ['MIT']
        websiteUrl = "${gitUrl}"
        issueTrackerUrl = "${gitUrl}/issues"
        vcsUrl = "${gitUrl}.git"
        version {
            name = "$project.version"
            desc = "$project.version"
            released = new Date ()
            vcsTag = project.version
        }
    }
}
*/