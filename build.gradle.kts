plugins {
    id("java")
}

repositories {
    mavenCentral()
}

subprojects {

    apply {
        plugin("java")
    }

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
    }

    dependencies {
        compileOnly("commons-io:commons-io:2.16.1")
        compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    }

    tasks.compileJava {
        options.encoding = "UTF-8"
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }

}
