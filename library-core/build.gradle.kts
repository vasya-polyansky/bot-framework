val arrow_version: String by project
val kotlin_logging_version: String by project
val ktor_server_version: String by project
val logback_version: String by project
val kotlinx_coroutines_version: String by project


dependencies {
    api("io.arrow-kt:arrow-core:$arrow_version")
    api("io.github.microutils:kotlin-logging:$kotlin_logging_version")

    implementation("io.ktor:ktor-server-core:$ktor_server_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinx_coroutines_version")
}
