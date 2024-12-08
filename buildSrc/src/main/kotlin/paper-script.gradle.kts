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
    paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT")
    compileOnly("de.miraculixx:kpaper:1.1.1")
    implementation("dev.jorel:commandapi-bukkit-shade:9.7.0")
    implementation("dev.jorel:commandapi-bukkit-kotlin:9.7.0")
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
}
