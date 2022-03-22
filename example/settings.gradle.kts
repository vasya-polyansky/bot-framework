rootProject.name = "example"


// Using the local project here for dependencies.
// Remove this `includeBuild` in a real project.
includeBuild("../") {
    dependencySubstitution {
        val group = "io.github.vasya-polyansky"
        substitute(module("$group:bot-framework-core")).using(project(":bot-framework-core"))
        substitute(module("$group:bot-framework-telegram")).using(project(":bot-framework-telegram"))
    }
}
