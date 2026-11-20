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
    paperweight.paperDevBundle("26.3.build.+")
    compileOnly("de.miraculixx:kpaper:1.2.1")
    compileOnly("dev.jorel:commandapi-paper-core:12.1.0")
    implementation("dev.jorel:commandapi-kotlin-paper:12.1.0")
    implementation("dev.jorel:commandapi-kotlin-core:12.1.0")
    implementation("dev.jorel:commandapi-paper-shade:12.1.0") {
        exclude(group = "dev.jorel", module = "commandapi-paper-vh")
        exclude(group = "dev.jorel", module = "commandapi-paper-mojang-mapped")
        exclude(group = "dev.jorel", module = "commandapi-paper-nms-dependency")
    }
}

paperweight {
    javaLauncher = javaToolchains.launcherFor {
        languageVersion = JavaLanguageVersion.of(25)
    }
}
