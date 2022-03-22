val tgbotapi_version: String by project

dependencies {
    api(project(":bot-framework-core"))

    api("dev.inmo:tgbotapi.core:$tgbotapi_version")
    api("dev.inmo:tgbotapi.extensions.api:$tgbotapi_version")
    api("dev.inmo:tgbotapi.extensions.utils:$tgbotapi_version")
    implementation(kotlin("stdlib"))
}
