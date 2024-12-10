package de.miraculixx.bmbm

import de.bluecolored.bluemap.api.BlueMapAPI
import de.miraculixx.bmbm.commands.OverviewCommand
import de.miraculixx.bmbm.map.MarkerManager
import de.miraculixx.bmbm.map.events.BlockBreakListener
import de.miraculixx.bmbm.map.events.BlockPlaceListener
import de.miraculixx.bmbm.map.gui.ClickManager
import de.miraculixx.bmbm.utils.APIConnector
import de.miraculixx.bmbm.utils.GlobalListener
import de.miraculixx.bmbm.utils.Listener
import de.miraculixx.bmbm.utils.cache.MarkerImages
import de.miraculixx.bmbm.utils.config.ConfigManager
import de.miraculixx.bmbm.utils.config.Configs
import de.miraculixx.kpaper.localization.Localization
import de.miraculixx.kpaper.main.KPaper
import de.miraculixx.kpaper.main.KPaperConfiguration
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.function.Consumer

class Main : KPaper() {
    companion object {
        lateinit var INSTANCE: KPaper
        lateinit var localization: Localization
    }

    private lateinit var listener: List<Listener>
    private lateinit var assetsLoader: MarkerImages
    private lateinit var localizer: Localization

    override fun load() {
        CommandAPI.onLoad(CommandAPIBukkitConfig(this).silentLogs(true))

        dataFolder.mkdir()

//        CoroutineScope(Dispatchers.Default).launch {
//            APIConnector.checkVersion(description.version.toIntOrNull() ?: 0)
//        }
    }

    override fun startup() {
        INSTANCE = this
        CommandAPI.onEnable()
        server.pluginManager.registerEvents(GlobalListener, this)

        // Setup
        KPaperConfiguration.Events.autoRegistration = false

        // Load Content
        assetsLoader = MarkerImages()
        listener = listOf(BlockBreakListener(), BlockPlaceListener(), ClickManager())
        OverviewCommand()

        BlueMapAPI.onEnable(onBlueMapEnable)
        BlueMapAPI.onDisable(onBlueMapDisable)
    }

    override fun shutdown() {
        CommandAPI.onDisable()
        BlueMapAPI.unregisterListener(onBlueMapEnable)
        BlueMapAPI.unregisterListener(onBlueMapDisable)
        MarkerManager.saveAllMarker()
        logger.info("Successfully saved all data! Good Bye :)")
    }

    private val onBlueMapEnable = Consumer<BlueMapAPI> {
        logger.info("Connect to BlueMap API...")
        assetsLoader.loadImages(it)
        MarkerManager.loadAllMarker(it)
        Configs.entries.forEach { c -> ConfigManager.reload(c) }
        val config = ConfigManager.getConfig(Configs.SETTINGS)
        val languages = listOf("en_US", "de_DE").map { it to javaClass.getResourceAsStream("/language/$it.yml") }
        localization = Localization(File("${dataFolder}/language"), config.getString("language") ?: "en_US", languages)
        listener.forEach { listener -> listener.register() }
        logger.info("Successfully enabled Banner Marker addition!")
    }

    private val onBlueMapDisable = Consumer<BlueMapAPI> {
        logger.info("Disconnecting from BlueMap API...")
        listener.forEach { listener -> listener.unregister() }
        assetsLoader.unloadImages()
        MarkerManager.saveAllMarker()
        logger.info("Successfully saved all data. Waiting for BlueMap to reload...")
    }
}

val PluginManager by lazy { Main.INSTANCE }