
plugins {
    `kotlin-script`
    `paper-script`
    `shadow-script`
}

dependencies {
//    implementation(project(":vanilla"))
}

version = "1.0"

sourceSets {
    main {
        resources.srcDirs("$rootDir/commons/")
    }
}

group = "de.miraculixx.bmbm"
setProperty("module_name", "bmbm")