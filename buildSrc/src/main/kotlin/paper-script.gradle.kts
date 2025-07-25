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
    paperweight.paperDevBundle("1.21.5-R0.1-SNAPSHOT")
    compileOnly("de.miraculixx:kpaper:1.2.1")
    implementation("dev.jorel:commandapi-bukkit-shade:10.1.1")
    implementation("dev.jorel:commandapi-bukkit-kotlin:10.1.1")
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
}
