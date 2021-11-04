val tgbotapi_version: String by project


dependencies {
    api(project(":library-core"))

    api("dev.inmo:tgbotapi:$tgbotapi_version")
}