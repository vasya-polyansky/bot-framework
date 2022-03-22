import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    id("maven-publish")
    id("signing")
    id("org.jetbrains.dokka") version "1.6.10"
}

allprojects {
    group = "io.github.vasya-polyansky"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
            freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
        }
    }
}

subprojects {
    val currentProject = this

    apply {
        plugin("org.jetbrains.kotlin.jvm")
        plugin("maven-publish")
        plugin("signing")
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

    signing {
        useInMemoryPgpKeys(
            stringProperty("gpg.private.key") ?: return@signing,
            stringProperty("gpg.private.password") ?: return@signing
        )
        sign(publishing.publications)
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
                artifact(javadocJar)

                pom {
                    name.set(currentProject.name)
                    description.set(currentProject.name)
                    licenses {
                        license {
                            name.set("MIT")
                            url.set("https://opensource.org/licenses/MIT")
                        }
                    }
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
                setUrl(
                    if (version.toString().endsWith("SNAPSHOT")) {
                        "https://s01.oss.sonatype.org/content/repositories/snapshots"
                    } else {
                        "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2"
                    }
                )
                credentials {
                    username = stringProperty("sonatype.username")
                    password = stringProperty("sonatype.password")
                }
            }
        }
    }
}

fun stringProperty(propertyName: String): String? {
    return System.getProperty(propertyName)
}
