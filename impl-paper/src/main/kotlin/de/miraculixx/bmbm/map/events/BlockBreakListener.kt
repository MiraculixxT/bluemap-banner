package de.miraculixx.bmbm.map.events

import com.flowpowered.math.vector.Vector3d
import de.miraculixx.bmbm.Listener
import de.miraculixx.bmbm.map.MarkerManager
import de.miraculixx.bmbm.utils.config.ConfigManager
import de.miraculixx.bmbm.utils.config.Configs
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.localization.msg
import de.miraculixx.kpaper.runnables.taskRunLater
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPhysicsEvent

class BlockBreakListener : Listener {
    private val onBlockBreak = listen<BlockBreakEvent> {
        val block = it.block
        val player = it.player
        val uuid = player.uniqueId
        val config = ConfigManager.getConfig(Configs.SETTINGS)
        if (!block.type.name.endsWith("_BANNER")) return@listen
        val vector = Vector3d.from(block.x.toDouble(), block.y.toDouble(), block.z.toDouble())
        val markerOwner = MarkerManager.getMarkerOwner(vector)

        val max = MarkerManager.getMaxAmount(player)
        val markerCount = MarkerManager.getMarkers(uuid).size
        if (markerOwner == player.uniqueId && config.getBoolean("notify-player"))
            player.sendMessage(msg("event.break", listOf(markerCount.minus(1).toString(), if (max != -1) max.toString() else "∞")))

        MarkerManager.removeMarker(vector, block.world.name, uuid)
    }

    private val onBlockPhysics = listen<BlockPhysicsEvent> {
        val block = it.block
        if (!Tag.BANNERS.isTagged(block.type)) return@listen
        val loc = block.location
        // Vvv This is awful. If anyone knows a better solution, please DM me! Vvv
        taskRunLater(1, false) {
            if (loc.block.type == Material.AIR) {
                val vector = Vector3d.from(loc.x, loc.y, loc.z)
                val owner = MarkerManager.getMarkerOwner(vector) ?: return@taskRunLater
                MarkerManager.removeMarker(vector, loc.world.name, owner)
            }
        }
    }


    override fun register() {
        onBlockBreak.register()
        onBlockPhysics.register()
    }

    override fun unregister() {
        onBlockBreak.unregister()
        onBlockPhysics.unregister()
    }
}