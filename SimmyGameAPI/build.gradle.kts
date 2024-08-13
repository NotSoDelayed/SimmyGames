
plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version("8.1.1")
}

group = "me.delayedgaming.simmygameapi"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
//    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    implementation("org.jetbrains:annotations:24.1.0")
    compileOnly("org.github.paperspigot:paperspigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("commons-io:commons-io:2.16.1")
    implementation("fr.mrmicky:fastboard:2.1.3")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.shadowJar {
    archiveFileName = project.name + '-' + project.version + ".jar"
    relocate("fr.mrmicky.fastboard", "me.notsodelayed.simmygamesapi.api")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}
