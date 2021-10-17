import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val tgbotapi_version: String by project
val kotlinx_coroutines: String by project

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
    implementation("dev.inmo:tgbotapi:$tgbotapi_version")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}