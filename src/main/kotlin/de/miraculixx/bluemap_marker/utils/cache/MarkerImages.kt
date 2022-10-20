package de.miraculixx.bluemap_marker.utils.cache

import de.bluecolored.bluemap.api.BlueMapAPI
import de.bluecolored.bluemap.api.WebApp
import de.miraculixx.bluemap_marker.PluginManager
import org.bukkit.DyeColor
import java.io.File
import javax.imageio.ImageIO

lateinit var bannerImages: Map<DyeColor, String>

class MarkerImages {
    private val dataFolder = PluginManager.dataFolder.path
    private val assetsFolder = File("$dataFolder/assets")

    fun loadImages(blueMapAPI: BlueMapAPI) {
        val webApp = blueMapAPI.webApp
        bannerImages = buildMap {
            DyeColor.values().forEach { put(it, loadImage("marker_${it.name}.png", webApp)) }
        }
    }

    fun unloadImages() {
        bannerImages = emptyMap()
    }

    private fun loadImage(name: String, webApp: WebApp): String {
        val imgFile = File("$assetsFolder/$name")
        return webApp.createImage(ImageIO.read(imgFile), imgFile.path)
    }

    init {
        val assetsFolder = File("$dataFolder/assets")
        assetsFolder.mkdirs()
        val mainClass = this::class.java
        DyeColor.values().forEach { dye ->
            val assetName = "marker_${dye.name}.png"
            val targetFile = File("${assetsFolder.path}/$assetName")
            if (targetFile.exists()) return@forEach //Marker texture is already present, skip...

            val stream = mainClass.getResourceAsStream("/assets/marker_${dye.name}.png")
            if (stream == null) {
                PluginManager.logger.warning("Failed to load $assetName asset! Please report this bug to https://mutils.de/dc")
                return@forEach
            }
            targetFile.writeBytes(stream.readAllBytes())
        }
    }
}