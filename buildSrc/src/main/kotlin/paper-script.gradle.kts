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
    paperweight.paperDevBundle("1.21.11-R0.1-SNAPSHOT")
    compileOnly("de.miraculixx:kpaper:1.2.1")
    compileOnly("dev.jorel:commandapi-paper-core:11.0.0")
    implementation("dev.jorel:commandapi-kotlin-paper:11.0.0")
    implementation("dev.jorel:commandapi-kotlin-core:11.0.0")
    implementation("dev.jorel:commandapi-paper-shade:11.0.0")



}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
}
