import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.modrinth.minotaur")
}

repositories {
    mavenCentral()
    maven ( "https://jitpack.io" )
    maven ( "https://repo.bluecolored.de/releases" )
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    compileOnly ("de.bluecolored:bluemap-api:2.7.5")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(21)
    }
    compileKotlin {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
    }
}

version = "1.1"

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set(name) // Project ID or the slug
    versionNumber.set(version as String)
    versionType.set("release")
    uploadFile.set(tasks.build)
    gameVersions.addAll("1.20.4")
    loaders.add("fabric")
    dependencies {
        // The scope can be `required`, `optional`, `incompatible`, or `embedded`
        // The type can either be `project` or `version`
        required.project("bluemap")
    }
}