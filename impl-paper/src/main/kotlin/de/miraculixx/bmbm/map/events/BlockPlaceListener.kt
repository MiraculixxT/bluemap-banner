package de.miraculixx.bmbm.map.events

import de.bluecolored.bluemap.api.markers.POIMarker
import de.miraculixx.bmbm.utils.Listener
import de.miraculixx.bmbm.map.MarkerManager
import de.miraculixx.bmbm.utils.cache.bannerImages
import de.miraculixx.bmbm.utils.config.ConfigManager
import de.miraculixx.bmbm.utils.config.Configs
import de.miraculixx.bmbm.utils.messages.cError
import de.miraculixx.bmbm.utils.messages.plainSerializer
import de.miraculixx.bmbm.utils.messages.prefix
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.bukkit.cmp
import de.miraculixx.kpaper.extensions.bukkit.plus
import de.miraculixx.kpaper.extensions.bukkit.toLegacyString
import de.miraculixx.kpaper.extensions.console
import de.miraculixx.kpaper.items.name
import de.miraculixx.kpaper.localization.msg
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.event.block.BlockPlaceEvent

class BlockPlaceListener : Listener {
    private var blacklistedWords: Set<String> = mutableSetOf()

    private val onPlace = listen<BlockPlaceEvent> {
        val block = it.block
        if (!block.type.name.endsWith("_BANNER")) return@listen
        val player = it.player
        val uuid = it.player.uniqueId
        val config = ConfigManager.getConfig(Configs.SETTINGS)
        val blockedWorlds = config.getStringList("disabled-worlds")
        val sneakBlock = config.getBoolean("sneak-blocking")
        val worldName = player.world.name

        // Check if the banner has a name - Only named banners create markers
        val item = it.itemInHand
        val name = item.itemMeta?.name ?: return@listen

        if (blacklistedWords.contains(name.toLegacyString())) {
            // Do not do anything on blacklist
            return@listen
        }

        // Check blocked worlds
        if (blockedWorlds.contains(worldName)) {
            if (config.getBoolean("notify-player")) player.sendMessage(msg("blocked-world", listOf(worldName)))
            return@listen
        }

        if (sneakBlock && player.isSneaking) {
            player.sendMessage(msg("event.sneaked"))
            return@listen
        }

        // Check max marker count
        val max = MarkerManager.getMaxAmount(player)
        val markerCount = MarkerManager.getMarkers(uuid).size
        if (max != -1 && markerCount >= max) {
            if (!player.isSneaking) {
                it.isCancelled = true
                player.sendMessage(msg("event.limited", listOf(config.getString("max-marker-per-player") ?: "0")))
            }
            return@listen
        }

        if (config.getBoolean("notify-player"))
            player.sendMessage(msg("event.place", listOf(markerCount.plus(1).toString(), if (max != -1) max.toString() else "∞")))

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

        if (icon == null) {
            console.sendMessage(prefix + cmp("Failed to load image for banner color ${color.name}! Check if 'assets/marker_${color.name}.png' exist!", cError))
            return@listen
        }

        val newMarker = POIMarker.builder().apply {
            val labelAddition = config.getString("label-suffix")?.replace("<player>", player.name) ?: ""
            label(plainSerializer.serialize(name) + labelAddition)
            icon(icon.content, icon.width / 2, icon.height)
            position(block.x.toDouble(), block.y.toDouble(), block.z.toDouble())

            val maxDistance = config.getDouble("max-view-distance")
            if (maxDistance > 0) maxDistance(maxDistance)
        }.build()

        MarkerManager.addMarker(newMarker, block.world.name, uuid)
    }

    override fun register() {
        onPlace.register()
        val config = ConfigManager.getConfig(Configs.SETTINGS)
        blacklistedWords = config.getStringList("blocked-words").toSet()
    }

    override fun unregister() {
        onPlace.unregister()
    }
}