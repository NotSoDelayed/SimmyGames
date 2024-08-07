
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
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("commons-io:commons-io:2.16.1")
    implementation("fr.mrmicky:fastboard:2.1.3")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.shadowJar {
    relocate("fr.mrmicky.fastboard", "com.yourpackage.fastboard")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}
