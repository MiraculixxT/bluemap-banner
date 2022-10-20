package de.miraculixx.bluemap_marker.utils.config

import org.bukkit.configuration.file.FileConfiguration

object ConfigManager {
    /*
    Config Files that are used in many Functions or in Objects
    are stored centrally here to prevent double loading.
     */

    //Config Files
    private val configMap = HashMap<Configs, Config>()

    init {
        // Loading Default Configs
        configMap[Configs.SETTINGS] = Config("settings")
        configMap[Configs.LANGUAGE] = Config("messages")
    }

    fun reload(type: Configs) {
        configMap[type]?.reload()
    }

    fun saveAll() {
        configMap.forEach { (_, file) ->
            file.save()
        }
    }

    fun save(type: Configs) {
        configMap[type]?.save()
    }

    fun reset(type: Configs) {
        configMap[type]?.reset()
    }

    fun getConfig(type: Configs): FileConfiguration {
        return configMap[type]?.getConfig() ?: configMap[Configs.SETTINGS]!!.getConfig()
    }

    fun getConfigInstance(type: Configs): Config {
        return configMap[type] ?: configMap[Configs.SETTINGS]!!
    }
}