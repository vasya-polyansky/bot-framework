import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.0"
}

allprojects {
    plugins.apply("org.jetbrains.kotlin.jvm")

    group = "io.github.vasya-polyansky"
    version = "0.1.0"

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation(kotlin("stdlib"))
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
            freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
        }
    }
}
