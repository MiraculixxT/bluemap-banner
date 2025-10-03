
rootProject.name = "BlueMap-BannerMarker"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://papermc.io/repo/repository/maven-public/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention").version("1.0.0")
}

//include("impl-fabric")
include("impl-paper")
//include("vanilla")