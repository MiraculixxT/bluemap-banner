package de.miraculixx.bmbm.utils.config

import de.miraculixx.bmbm.PluginManager
import de.miraculixx.bmbm.utils.messages.prefix
import net.axay.kspigot.extensions.console
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File


class Config(private val name: String) {

    private lateinit var configFile: File
    private lateinit var config: FileConfiguration

    init {
        load(false)
    }

    private fun load(reset: Boolean) {
        configFile = File(PluginManager.dataFolder, "$name.yml")
        if (!configFile.exists() || reset) {
            configFile.parentFile.mkdirs()
            PluginManager.saveResource("$name.yml", true)
            configFile.createNewFile()
        }
        config = YamlConfiguration()
        try {
            config.load(configFile)
        } catch (e: Exception) {
            e.printStackTrace()
            console.sendMessage("$prefix §c$name.yml Config failed to load! ^^ Reason above ^^")
            console.sendMessage("$prefix §cCopy and Save your §nlatest.log§f §cto get Support!")
        }
    }

    fun getConfig(): FileConfiguration {
        return config
    }

    fun save() {
        config.save(configFile)
    }

    fun reset() {
        configFile.delete()
        configFile.deleteOnExit()
        load(true)
    }

    fun reload(): FileConfiguration {
        load(false)
        return config
    }
}