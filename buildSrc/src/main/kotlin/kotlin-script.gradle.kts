import gradle.kotlin.dsl.accessors._a124e2c23f8e366779ee8f0a55e154c9.build

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.modrinth.minotaur")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
    maven ( "https://jitpack.io" )
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    compileOnly("com.github.BlueMap-Minecraft:BlueMapAPI:v2.5.1")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(17)
    }
    compileKotlin {
        kotlinOptions.jvmTarget = "17"
    }
}

version = "1."

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