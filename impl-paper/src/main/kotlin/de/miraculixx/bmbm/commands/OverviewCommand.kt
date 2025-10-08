package de.miraculixx.bmbm.commands

import de.bluecolored.bluemap.api.markers.POIMarker
import de.miraculixx.bmbm.PluginManager
import de.miraculixx.bmbm.map.MarkerManager
import de.miraculixx.bmbm.map.gui.storageBuilder
import de.miraculixx.bmbm.utils.messages.cHighlight
import de.miraculixx.bmbm.utils.messages.cMark
import de.miraculixx.kpaper.extensions.bukkit.cmp
import de.miraculixx.kpaper.extensions.bukkit.plus
import de.miraculixx.kpaper.extensions.worlds
import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.kpaper.localization.msg
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.jorel.commandapi.kotlindsl.asyncPlayerProfileArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType
import java.util.*

class OverviewCommand {
    val command = commandTree("bmbanner") {
        withPermission("bmb.overview")
        withAliases("bmb")
        literalArgument("global") {
            playerExecutor { player, _ ->
                val markers = MarkerManager.getMarkers()
                openGUI(player, null, markers)
            }
        }
        asyncPlayerProfileArgument("target") {
            playerExecutor { player, args ->
                val target = args[0] as OfflinePlayer
                val markers = MarkerManager.getMarkers(target.uniqueId)
                if (markers.isEmpty()) {
                    player.sendMessage(msg("command.no-marker", listOf(target.name ?: "Unknown")))
                    return@playerExecutor
                }
                openGUI(player, target, markers)
            }
        }
    }
    private fun openGUI(player: Player, target: OfflinePlayer?, markers: Map<POIMarker, String>) {
        storageBuilder {
            title = cmp("Banner Markers - ", cHighlight, bold = true) + cmp(target?.name ?: "Global", cHighlight)
            header = itemStack(Material.PLAYER_HEAD) {
                meta<SkullMeta> {
                    owningPlayer = target
                    name = cmp("${target?.name}'s Marker", cHighlight, bold = true)
                    lore(listOf(cmp(target?.uniqueId.toString(), NamedTextColor.DARK_GRAY)))
                }
            }
            filterable = true
            items = buildList {
                markers.forEach { (marker, worldName) ->
                    val world = worlds.firstOrNull { it.name.equals(worldName, true) } ?: return@forEach
                    val material = when (world.environment) {
                        World.Environment.NORMAL -> Material.GRASS_BLOCK
                        World.Environment.NETHER -> Material.NETHERRACK
                        World.Environment.THE_END -> Material.END_STONE
                        World.Environment.CUSTOM -> Material.CRAFTING_TABLE
                    }
                    add(
                        itemStack(material) {
                            meta {
                                name = cmp(marker.label, cHighlight)
                                customModel = 1
                                val vector = marker.position
                                persistentDataContainer.set(
                                    NamespacedKey(PluginManager, "marker-${UUID.randomUUID()}"),
                                    PersistentDataType.STRING,
                                    "${vector.x}:${vector.y}:${vector.z}:$worldName:${target?.uniqueId}"
                                )
                                lore(
                                    listOf(
                                        cmp("World: ", cMark) + cmp(world.name),
                                        cmp("Location: ", cMark) + cmp("${vector.x} ${vector.y} ${vector.z}"),
                                        cmp("Marker Type: ", cMark) + cmp(marker.type),
                                        Component.empty(),
                                        cmp("Click » ") + cmp("Teleport", cMark),
                                        cmp("Shift Click » ") + cmp("Delete Marker", cMark)
                                    )
                                )
                            }
                        }
                    )
                }
            }
        }.open()
        player.playSound(player, Sound.BLOCK_ENDER_CHEST_OPEN, 1f, 1f)
    }
}