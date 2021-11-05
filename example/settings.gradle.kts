rootProject.name = "example"


includeBuild("../") {
    dependencySubstitution {
        val group = "io.github.vasya-polyansky"
        substitute(module("$group:library-core")).using(project(":library-core"))
        substitute(module("$group:library-telegram")).using(project(":library-telegram"))
    }
}
