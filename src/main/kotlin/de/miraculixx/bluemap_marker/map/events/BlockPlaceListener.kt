package de.miraculixx.bluemap_marker.map.events

import de.bluecolored.bluemap.api.markers.POIMarker
import de.miraculixx.bluemap_marker.map.MarkerManager
import de.miraculixx.bluemap_marker.utils.cache.bannerImages
import de.miraculixx.bluemap_marker.utils.config.ConfigManager
import de.miraculixx.bluemap_marker.utils.config.Configs
import de.miraculixx.bluemap_marker.utils.interfaces.Listener
import de.miraculixx.bluemap_marker.utils.messages.msg
import de.miraculixx.bluemap_marker.utils.messages.plainSerializer
import net.axay.kspigot.event.SingleListener
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import net.axay.kspigot.items.name
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.event.block.BlockPlaceEvent

class BlockPlaceListener : Listener<BlockPlaceEvent> {
    override val listener: SingleListener<BlockPlaceEvent> = listen(register = false) {
        val block = it.block
        if (!block.type.name.endsWith("_BANNER")) return@listen
        val player = it.player
        val uuid = it.player.uniqueId
        val config = ConfigManager.getConfig(Configs.SETTINGS)
        val blockedWorlds = config.getStringList("disabled-worlds")
        val worldName = player.world.name
        if (blockedWorlds.contains(worldName)) {
            if (config.getBoolean("notify-player")) player.sendMessage(msg("blocked-world", input = listOf(worldName)))
        }

        val max = config.getInt("max-marker-per-player")
        val markerCount = MarkerManager.getMarkers(uuid).size
        if (max != -1 && markerCount >= max) {
            if (!player.isSneaking) {
                it.isCancelled = true
                player.sendMessage(msg("event.limited", listOf(config.getString("max-marker-per-player") ?: "0")))
            }
            return@listen
        }

        if (config.getBoolean("notify-player")) player.sendMessage(msg("event.place", listOf(markerCount.toString(), max.toString())))

        val item = it.itemInHand
        val name = item.itemMeta?.name ?: return@listen

        val color = when (block.type) {
            Material.WHITE_BANNER, Material.WHITE_WALL_BANNER -> DyeColor.WHITE
            Material.ORANGE_BANNER, Material.ORANGE_WALL_BANNER -> DyeColor.ORANGE
            Material.MAGENTA_BANNER, Material.MAGENTA_WALL_BANNER -> DyeColor.MAGENTA
            Material.LIGHT_BLUE_BANNER, Material.LIGHT_BLUE_WALL_BANNER -> DyeColor.LIGHT_BLUE
            Material.YELLOW_BANNER, Material.YELLOW_WALL_BANNER -> DyeColor.YELLOW
            Material.LIME_BANNER, Material.LIME_WALL_BANNER -> DyeColor.LIME
            Material.PINK_BANNER, Material.PINK_WALL_BANNER -> DyeColor.PINK
            Material.GRAY_BANNER, Material.GRAY_WALL_BANNER -> DyeColor.GRAY
            Material.LIGHT_GRAY_BANNER, Material.LIGHT_GRAY_WALL_BANNER -> DyeColor.LIGHT_GRAY
            Material.CYAN_BANNER, Material.CYAN_WALL_BANNER -> DyeColor.CYAN
            Material.PURPLE_BANNER, Material.PURPLE_WALL_BANNER -> DyeColor.PURPLE
            Material.BLUE_BANNER, Material.BLUE_WALL_BANNER -> DyeColor.BLUE
            Material.BROWN_BANNER, Material.BROWN_WALL_BANNER -> DyeColor.BROWN
            Material.GREEN_BANNER, Material.GREEN_WALL_BANNER -> DyeColor.GREEN
            Material.RED_BANNER, Material.RED_WALL_BANNER -> DyeColor.RED
            Material.BLACK_BANNER, Material.BLACK_WALL_BANNER -> DyeColor.BLACK
            else -> DyeColor.WHITE
        }
        val icon = bannerImages[color]

        val newMarker = POIMarker.toBuilder()
            .label(plainSerializer.serialize(name))
            .icon(icon, 0, 0)
            .maxDistance(500.0)
            .position(block.x, block.y, block.z)
            .build()

        MarkerManager.addMarker(newMarker, block.world.name, uuid)
    }

    override fun register() {
        listener.register()
    }
}