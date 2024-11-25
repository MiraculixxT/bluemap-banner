plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}


tasks {
    shadowJar {
        dependencies {
            val moduleName = properties["module_name"]
            include {
                val split = it.moduleGroup.split('.')
                val prefix = "${split.getOrNull(0)}.${split.getOrNull(1)}"
                val isAPI = split.lastOrNull() == "api"
                val isModuleAPI = moduleName == split.getOrNull(split.size - 2)
                (prefix == "de.miraculixx" && (!isAPI || isModuleAPI)) || prefix == "dev.jorel"
            }
        }
        val moduleName = properties["module_name"]
        relocate("de.miraculixx.vanilla", "de.miraculixx.$moduleName.vanilla")
    }
}