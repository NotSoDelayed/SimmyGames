plugins {
    id("java")
}

group = "me.notsodelayed.thenexus"
version = "1.0-SNAPSHOT"

dependencies {
    compileOnly(project(":SimmyGameAPI")) {
        isTransitive = true
    }
}

tasks.compileJava {
    dependsOn(":SimmyGameAPI:shadowJar")
}
