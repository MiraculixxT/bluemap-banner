plugins {
    kotlin("jvm") version "1.8.22"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.20"
}

group = "de.miraculixx"
version = "1.0.1"

repositories {
    maven {
        name = "JitPack"
        setUrl("https://jitpack.io")
    }
    mavenCentral()
}

dependencies {
    implementation("com.github.BlueMap-Minecraft", "BlueMapAPI", "v2.1.0")
}
