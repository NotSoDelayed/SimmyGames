plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version("8.1.1")
}

group = "me.notsodelayed.thenexus"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "CodeMC"
        url = uri("https://repo.codemc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly(project(":SimmyGameAPI"))
    implementation("de.tr7zw:item-nbt-api:2.12.3")
    compileOnly("commons-io:commons-io:2.15.1")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.shadowJar {
    relocate("de.tr7zw.changeme.nbtapi", "me.notsodelayed.thenexus.api.nbtapi")
}
