rootProject.name = "example"


// Using the local project here for dependencies.
// Remove this `includeBuild` in a real project.
includeBuild("../") {
    dependencySubstitution {
        val group = "io.github.vasya-polyansky"
        substitute(module("$group:library-core")).using(project(":library-core"))
        substitute(module("$group:library-telegram")).using(project(":library-telegram"))
    }
}
