plugins {
    id("com.gradleup.shadow") version("8.3.2")
}

group = "me.delayedgaming.simmygameapi"
version = "1.0-SNAPSHOT"

repositories {
    maven {
        name = "CodeMC"
        url = uri("https://repo.codemc.io/repository/maven-public/")
    }
    maven("https://jitpack.io")
}

dependencies {
    implementation("fr.mrmicky:fastboard:2.1.3")
    implementation("fr.mrmicky:FastInv:3.1.1")
    implementation("de.tr7zw:item-nbt-api:2.13.2")
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        archiveFileName = project.name + '-' + project.version + ".jar"
        relocate("fr.mrmicky.fastboard", "me.notsodelayed.simmygameapi.lib.fastboard")
        relocate("fr.mrmicky.fastinv", "me.notsodelayed.simmygameapi.lib.fastinv")
        relocate("de.tr7zw.changeme.nbtapi", "me.notsodelayed.simmygameapi.lib.nbtapi")
    }
}
