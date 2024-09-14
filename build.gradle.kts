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
        maven("https://jitpack.io")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://oss.sonatype.org/content/groups/public/")
    }

    dependencies {
        compileOnly("org.jetbrains:annotations:24.1.0")
        compileOnly("commons-io:commons-io:2.16.1")
        compileOnly("org.github.paperspigot:paperspigot-api:1.8.8-R0.1-SNAPSHOT")
    }

    tasks.compileJava {
        options.encoding = "UTF-8"
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }

}
