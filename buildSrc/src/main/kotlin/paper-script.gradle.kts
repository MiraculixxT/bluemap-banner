plugins {
    kotlin("jvm")
    id("io.papermc.paperweight.userdev")
    id("xyz.jpenilla.run-paper")
}

repositories {
    mavenCentral()
    maven("https://repo.codemc.org/repository/maven-public/")
}

dependencies {
    paperweight.paperDevBundle("1.20.1-R0.1-SNAPSHOT")
    implementation("de.miraculixx:kpaper:1.1.0")
    implementation("dev.jorel:commandapi-bukkit-shade:9.0.3")
    implementation("dev.jorel:commandapi-bukkit-kotlin:9.0.3")
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
}
