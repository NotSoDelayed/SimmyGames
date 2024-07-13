
plugins {
    id("java")
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
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}
