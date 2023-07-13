
plugins {
    `kotlin-script`
    `paper-script`
    `shadow-script`
}

dependencies {
    implementation(project(":vanilla"))
}

sourceSets {
    main {
        resources.srcDirs("$rootDir/commons/")
    }
}

group = "de.miraculixx.bmbm"
setProperty("module_name", "bmbm")