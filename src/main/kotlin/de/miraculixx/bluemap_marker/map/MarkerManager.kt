package de.miraculixx.bluemap_marker.map

import com.flowpowered.math.vector.Vector3d
import com.google.gson.JsonSyntaxException
import de.bluecolored.bluemap.api.BlueMapAPI
import de.bluecolored.bluemap.api.gson.MarkerGson
import de.bluecolored.bluemap.api.markers.MarkerSet
import de.bluecolored.bluemap.api.markers.POIMarker
import de.miraculixx.bluemap_marker.PluginManager
import de.miraculixx.bluemap_marker.utils.config.ConfigManager
import de.miraculixx.bluemap_marker.utils.config.Configs
import de.miraculixx.bluemap_marker.utils.serializer.json
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import net.axay.kspigot.extensions.bukkit.warn
import net.axay.kspigot.extensions.console
import net.axay.kspigot.extensions.worlds
import net.axay.kspigot.languageextensions.kotlinextensions.createIfNotExists
import org.bukkit.entity.Player
import java.io.File
import java.util.*

object MarkerManager {
    private val markerSets: MutableMap<String, MarkerSet> = mutableMapOf()
    private val playerMarkers: MutableMap<UUID, MutableList<Vector3d>> = mutableMapOf()
    private val rankPermissions: MutableMap<String, Int> = mutableMapOf()
    private var defaultPermission = -1

    fun addMarker(marker: POIMarker, worldName: String, playerUUID: UUID) {
        val markerSet = markerSets["BANNER_MARKER_$worldName"]

        if (markerSet == null) {
            console.warn("Failed to apply marker to $worldName! Please reload BlueMap after creating new worlds")
            return
        }

        markerSet.markers[marker.position.toString()] = marker
        playerMarkers.getOrPut(playerUUID) {
            mutableListOf(marker.position)
        }.add(marker.position)
    }

    fun removeMarker(marker: Vector3d, worldName: String, playerUUID: UUID): Boolean {
        val markerSet = markerSets["BANNER_MARKER_$worldName"] ?: return false
        playerMarkers[playerUUID]?.remove(marker)
        return markerSet.markers.remove(marker.toString()) != null
    }

    fun getMarkerOwner(vector3d: Vector3d): UUID? {
        playerMarkers.forEach { (uuid, vectors) ->
            if (vectors.contains(vector3d)) return uuid
        }
        return null
    }

    fun getMarkers(playerUUID: UUID): Map<POIMarker, String> {
        return buildMap {
            val playerMarkers = playerMarkers[playerUUID] ?: return emptyMap()
            markerSets.forEach { (worldSet, sets) ->
                val worldName = worldSet.removePrefix("BANNER_MARKER_")
                sets.markers.forEach { (_, marker) ->
                    if (playerMarkers.contains(marker.position)) (marker as? POIMarker)?.let { put(it, worldName) }
                }
            }
        }
    }

    fun loadAllMarker(blueMapAPI: BlueMapAPI) {
        val gson = MarkerGson.INSTANCE
        val logger = PluginManager.logger
        val config = ConfigManager.getConfig(Configs.SETTINGS)
        val folder = prepareConfigFolder()
        worlds.forEach { world ->
            val worldName = world.name
            val markerFile = File("${folder.path}/${worldName}.json")
            val set = if (markerFile.exists()) {
                logger.info("Found markers for world '$worldName' - Loading ${markerFile.length() / 1000.0}kb")
                try {
                    gson.fromJson(markerFile.readText(), MarkerSet::class.java)
                } catch (e: JsonSyntaxException) {
                    logger.warning("Marker file for world $worldName is invalid! Skipping it...")
                    return@forEach
                }.apply {
                    label = config.getString("marker-set-label")
                    isToggleable = config.getBoolean("marker-set-toggleable")
                    isDefaultHidden = !config.getBoolean("marker-set-visible")
                }
            } else {
                MarkerSet.builder()
                    .defaultHidden(!config.getBoolean("marker-set-visible"))
                    .toggleable(config.getBoolean("marker-set-toggleable"))
                    .label(config.getString("marker-set-label"))
                    .build()
            }

            markerSets["BANNER_MARKER_$worldName"] = set
            blueMapAPI.getWorld(world.uid).ifPresent {
                it.maps.forEach { map ->
                    map.markerSets["BANNER_MARKER_${world.name}"] = set
                }
            }
        }
        val file = File("${folder.path}/player_markers.json")
        file.createIfNotExists()
        val content = file.readText()
        val playerMarkerMap = json.decodeFromString<MutableMap<UUID, MutableList<Vector3d>>>(content) //works
        playerMarkerMap.forEach { (uuid, markers) ->
            playerMarkers[uuid] = markers
        }

        // Load perms
        val ranks = config.getConfigurationSection("max-marker-per-player")
        ranks?.getKeys(false)?.forEach { perm ->
            val amount = ranks.getInt(perm)
            if (perm == "default") defaultPermission = amount
            else rankPermissions["banner-marker.max-marker.$perm"] = amount
        }
    }

    fun saveAllMarker() {
        val gson = MarkerGson.INSTANCE
        val folder = prepareConfigFolder()
        markerSets.forEach { (id, set) ->
            val file = File("${folder.path}/${id.removePrefix("BANNER_MARKER_")}.json")
            file.writeText(gson.toJson(set))
        }
        val file = File("$folder/player_markers.json")
        file.writeText(json.encodeToString(playerMarkers))
        markerSets.clear()
        playerMarkers.clear()
    }

    private fun prepareConfigFolder(): File {
        val sourceFolder = PluginManager.dataFolder
        if (!sourceFolder.exists()) sourceFolder.mkdir()
        val markerFolder = File("${sourceFolder.path}/marker")
        if (!markerFolder.exists()) markerFolder.mkdir()
        return markerFolder
    }

    fun getMaxAmount(player: Player): Int {
        var maxAmount = defaultPermission
        rankPermissions.forEach { (perm, amount) ->
            if (maxAmount < amount && player.hasPermission(perm)) maxAmount = amount
        }
        return maxAmount
    }
}