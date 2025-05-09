group = "me.notsodelayed.ultrahardcore"
version = "1.0-SNAPSHOT"

dependencies {
    compileOnly(project(":SimmyGameAPI")) {
        isTransitive = true
    }
}

tasks.compileJava {
    dependsOn(":SimmyGameAPI:shadowJar")
}
