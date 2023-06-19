plugins {
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.serialization") version "1.6.20"
    id("fabric-loom") version "1.2-SNAPSHOT"
    id("io.github.juuxel.loom-quiltflower") version "1.9.0"
}

repositories {
    maven {
        name = "JitPack"
        setUrl("https://jitpack.io")
    }
    mavenCentral()
}

val transitiveInclude: Configuration by configurations.creating {
    exclude(group = "com.mojang")
    exclude(group = "org.jetbrains.kotlin")
    exclude(group = "org.jetbrains.kotlinx")
}

dependencies {
    minecraft("com.mojang:minecraft:1.20.1")
    mappings(loom.officialMojangMappings())

    implementation(include(project(":vanilla"))!!)
    implementation("com.github.BlueMap-Minecraft", "BlueMapAPI", "v2.1.0")
    modImplementation("net.fabricmc:fabric-loader:0.14.21")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.83.1+1.20.1")
    modImplementation("net.fabricmc:fabric-language-kotlin:1.9.5+kotlin.1.8.22")
    modImplementation(include("net.kyori:adventure-platform-fabric:5.8.0")!!)
    val silkVersion = "1.10.0"
    modImplementation("net.silkmc:silk-commands:$silkVersion")
    modImplementation("net.silkmc:silk-core:$silkVersion")
    modImplementation("net.silkmc:silk-nbt:$silkVersion")
    transitiveInclude(implementation("org.yaml:snakeyaml:1.33")!!)

    transitiveInclude.resolvedConfiguration.resolvedArtifacts.forEach {
        include(it.moduleVersion.id.toString())
    }
}

loom {
    runs {
        named("server") {
            ideConfigGenerated(true)
        }
        named("client") {
            ideConfigGenerated(true)
        }
    }
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(17)
    }
}

kotlin {
    sourceSets {
        all {
            languageSettings.optIn("net.silkmc.silk.core.annotations.ExperimentalSilkApi")
        }
    }
}

group = "de.miraculixx"
