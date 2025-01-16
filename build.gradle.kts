plugins {
    id("java")
//    id("io.papermc.paperweight.userdev") version "1.7.7"
}

repositories {
    mavenCentral()
}

subprojects {

    apply {
        plugin("java")
//        plugin("io.papermc.paperweight.userdev")
    }

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
    }

    dependencies {
        compileOnly("commons-io:commons-io:2.16.1")
        compileOnly("io.papermc.paper:paper-api:1.20.6-R0.1-SNAPSHOT")
//        paperweight.paperDevBundle("1.20.6-R0.1-SNAPSHOT")
    }

    tasks {
//        build {
//            dependsOn(reobfJar)
//        }
        compileJava {
            options.encoding = "UTF-8"
        }
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }

}
