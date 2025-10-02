plugins {
    `kotlin-dsl`
    kotlin("plugin.serialization") version "2.1.0"
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.fabricmc.net/")
    maven("https://server.bbkr.space/artifactory/libs-release/")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://maven.quiltmc.org/repository/release/")
    maven("https://repo.codemc.org/repository/maven-public/")
}

dependencies {
    fun pluginDep(id: String, version: String) = "${id}:${id}.gradle.plugin:${version}"
    val kotlinVersion = "2.1.0"

    compileOnly(kotlin("gradle-plugin", kotlinVersion))
    runtimeOnly(kotlin("gradle-plugin", kotlinVersion))
    compileOnly(pluginDep("org.jetbrains.kotlin.plugin.serialization", kotlinVersion))
    runtimeOnly(pluginDep("org.jetbrains.kotlin.plugin.serialization", kotlinVersion))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.+")

    // Fabric implementation
    implementation("net.fabricmc:fabric-loom:1.8-SNAPSHOT")
    implementation(pluginDep("io.github.juuxel.loom-quiltflower", "1.9.0"))

    // Paper implementation
    implementation(pluginDep("io.papermc.paperweight.userdev", "2.0.0-beta.19"))
    implementation(pluginDep("xyz.jpenilla.run-paper", "3.0.0"))

    implementation(pluginDep("com.gradleup.shadow", "9.2.2"))
    implementation(pluginDep("com.modrinth.minotaur", "2.+"))
}