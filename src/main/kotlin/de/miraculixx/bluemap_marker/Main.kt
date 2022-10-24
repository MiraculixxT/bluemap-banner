package de.miraculixx.bluemap_marker

import de.bluecolored.bluemap.api.BlueMapAPI
import de.miraculixx.bluemap_marker.commands.OverviewCommand
import de.miraculixx.bluemap_marker.map.MarkerManager
import de.miraculixx.bluemap_marker.map.events.BlockBreakListener
import de.miraculixx.bluemap_marker.map.events.BlockPlaceListener
import de.miraculixx.bluemap_marker.map.gui.ClickManager
import de.miraculixx.bluemap_marker.utils.cache.MarkerImages
import de.miraculixx.bluemap_marker.utils.config.ConfigManager
import de.miraculixx.bluemap_marker.utils.config.Configs
import de.miraculixx.bluemap_marker.utils.interfaces.Listener
import net.axay.kspigot.main.KSpigot
import java.util.function.Consumer

class Main : KSpigot() {
    companion object {
        lateinit var INSTANCE: KSpigot
    }

    private lateinit var listener: List<Listener<*>>
    private lateinit var assetsLoader: MarkerImages

    override fun startup() {
        INSTANCE = this

        // Load Content
        assetsLoader = MarkerImages()
        listener = listOf(BlockBreakListener(), BlockPlaceListener(), ClickManager())
        OverviewCommand()

        BlueMapAPI.onEnable(onBlueMapEnable)
        BlueMapAPI.onDisable(onBlueMapDisable)
    }

    override fun shutdown() {
        BlueMapAPI.unregisterListener(onBlueMapEnable)
        BlueMapAPI.unregisterListener(onBlueMapDisable)
        MarkerManager.saveAllMarker()
        logger.info("Successfully saved all data! Good Bye :)")
    }

    private val onBlueMapEnable = Consumer<BlueMapAPI> {
        logger.info("Connect to BlueMap API...")
        assetsLoader.loadImages(it)
        MarkerManager.loadAllMarker(it)
        Configs.values().forEach { c -> ConfigManager.reload(c) }
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