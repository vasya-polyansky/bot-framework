import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val tgbotapi_version: String by project
val kotlinx_coroutines: String by project
val ktor_version: String by project
val kotlin_logging: String by project
val arrow_version: String by project

plugins {
    kotlin("jvm") version "1.5.30"
}

group = "me.vs"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinx_coroutines")
    implementation("io.github.microutils:kotlin-logging:$kotlin_logging")
    implementation("dev.inmo:tgbotapi:$tgbotapi_version")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.arrow-kt:arrow-core:$arrow_version")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}