val tgbotapi_version: String by project
val arrow_version: String by project

dependencies {
    api(project(":bot-framework-core"))

    api("dev.inmo:tgbotapi.core:$tgbotapi_version")
    api("dev.inmo:tgbotapi.api:$tgbotapi_version")
    api("dev.inmo:tgbotapi.utils:$tgbotapi_version")

    implementation(kotlin("stdlib"))
    implementation("io.arrow-kt:arrow-core:$arrow_version")
}
