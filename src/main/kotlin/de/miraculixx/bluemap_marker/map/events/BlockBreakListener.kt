package de.miraculixx.bluemap_marker.map.events

import com.flowpowered.math.vector.Vector3d
import de.miraculixx.bluemap_marker.map.MarkerManager
import de.miraculixx.bluemap_marker.utils.config.ConfigManager
import de.miraculixx.bluemap_marker.utils.config.Configs
import de.miraculixx.bluemap_marker.utils.interfaces.MultiListener
import de.miraculixx.bluemap_marker.utils.messages.msg
import net.axay.kspigot.event.SingleListener
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import net.axay.kspigot.runnables.taskRunLater
import org.bukkit.Material
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPhysicsEvent

class BlockBreakListener : MultiListener<BlockBreakEvent> {
    override val listener: SingleListener<BlockBreakEvent> = listen(register = false) {
        val block = it.block
        val player = it.player
        val uuid = player.uniqueId
        val config = ConfigManager.getConfig(Configs.SETTINGS)
        if (!block.type.name.endsWith("_BANNER")) return@listen
        val vector = Vector3d.from(block.x.toDouble(), block.y.toDouble(), block.z.toDouble())
        val markerOwner = MarkerManager.getMarkerOwner(vector)

        val max = config.getInt("max-marker-per-player")
        val markerCount = MarkerManager.getMarkers(uuid).size
        if (markerOwner == player.uniqueId && config.getBoolean("notify-player"))
            player.sendMessage(msg("event.break", listOf(markerCount.minus(1).toString(), if (max != -1) max.toString() else "∞")))

        MarkerManager.removeMarker(vector, block.world.name, uuid)
    }

    private val onBlockPhysics = listen<BlockPhysicsEvent>(register = false) {
        val block = it.block
        if (!block.type.name.endsWith("_BANNER")) return@listen
        val loc = block.location
        // vvv This is awful. If anyone know a better solution, please DM me! vvv
        taskRunLater(1, false) {
            if (loc.block.type == Material.AIR) {
                val vector = Vector3d.from(loc.x, loc.y, loc.z)
                val owner = MarkerManager.getMarkerOwner(vector) ?: return@taskRunLater
                MarkerManager.removeMarker(vector, loc.world.name, owner)
            }
        }
    }

    override val sideListener: List<SingleListener<*>> = listOf(onBlockPhysics)

    override fun register() {
        listener.register()
        onBlockPhysics.register()
    }
}