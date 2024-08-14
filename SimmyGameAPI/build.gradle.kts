plugins {
    id("com.github.johnrengelman.shadow") version("8.1.1")
}

group = "me.delayedgaming.simmygameapi"
version = "1.0-SNAPSHOT"

repositories {
    maven {
        name = "CodeMC"
        url = uri("https://repo.codemc.io/repository/maven-public/")
    }
}

dependencies {
    implementation("fr.mrmicky:fastboard:2.1.3")
    implementation("de.tr7zw:item-nbt-api:2.12.3")
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        archiveFileName = project.name + '-' + project.version + ".jar"
        relocate("fr.mrmicky.fastboard", "me.notsodelayed.simmygameapi.api.fastboard")
        relocate("de.tr7zw.changeme.nbtapi", "me.notsodelayed.simmygameapi.api.nbtapi")
    }
}
