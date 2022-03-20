import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    id("maven-publish")
    id("signing")
    id("org.jetbrains.dokka") version "1.6.10"
}

allprojects {
    group = "io.github.vasya-polyansky"
    version = "0.1.0"

    repositories {
        mavenCentral()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
        }
    }
}

subprojects {
    val project = this

    apply {
        plugin("org.jetbrains.kotlin.jvm")
        plugin("maven-publish")
        plugin("org.jetbrains.dokka")
    }

    java {
        withSourcesJar()
    }

    tasks.dokkaHtml.configure {
        outputDirectory.set(buildDir.resolve("dokka"))
    }

    val javadocJar by tasks.registering(Jar::class) {
        dependsOn(tasks.dokkaHtml)
        archiveClassifier.set("javadoc")
        from(tasks.dokkaHtml.get().outputDirectory)
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
                artifact(javadocJar)

                pom {
                    name.set(project.name)
                    url.set("https://github.com/vasya-polyansky/bot-framework")
                    developers {
                        developer {
                            id.set("vasya-polyansky")
                            name.set("Vasya Polyansky")
                            email.set("vasya1polyansky@gmail.com")
                        }
                    }

                    scm {
                        connection.set("scm:git:git@github.com:vasya-polyansky/bot-framework.git")
                        developerConnection.set("scm:git:git@github.com:vasya-polyansky/bot-framework.git")
                        url.set("https://github.com/vasya-polyansky/bot-framework.git")
                    }
                }
            }
        }

        repositories {
            mavenLocal()
            maven {
                name = "OSSRH"
                setUrl("https://oss.sonatype.org/service/local/staging/deploy/maven2")
                credentials {
                    username = System.getenv("SONATYPE_USERNAME")
                    password = System.getenv("SONATYPE_PASSWORD")
                }
            }
        }
    }
}