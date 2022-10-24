package de.miraculixx.bluemap_marker.map.gui

import com.flowpowered.math.vector.Vector3d
import de.miraculixx.bluemap_marker.map.MarkerManager
import de.miraculixx.bluemap_marker.utils.interfaces.Listener
import de.miraculixx.bluemap_marker.utils.messages.msg
import de.miraculixx.bluemap_marker.utils.messages.plainSerializer
import net.axay.kspigot.event.SingleListener
import net.axay.kspigot.event.listen
import net.axay.kspigot.items.customModel
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.persistence.PersistentDataType
import java.util.*

class ClickManager : Listener<InventoryClickEvent> {
    override val listener: SingleListener<InventoryClickEvent> = listen {
        val player = it.whoClicked as? Player ?: return@listen
        val title = plainSerializer.serialize(it.view.title())
        if (!title.startsWith("Banner Markers - ")) return@listen
        it.isCancelled = true
        val item = it.currentItem ?: return@listen
        if (item.itemMeta.customModel == 1) {
            val dataContainer = item.itemMeta.persistentDataContainer
            val key = dataContainer.keys.firstOrNull { key -> key.key.startsWith("marker-") } ?: return@listen
            val data = dataContainer.get(key, PersistentDataType.STRING) ?: return@listen
            val split = data.split(':')

            if (it.click.isShiftClick) {
                val vector = Vector3d(split[0].toDouble(), split[1].toDouble(), split[2].toDouble())
                val target = Bukkit.getOfflinePlayer(UUID.fromString(split[4]))
                MarkerManager.removeMarker(vector, split[3], target.uniqueId)
                player.sendMessage(msg("event.delete", listOf(target.name ?: player.name, split[3])))
                player.playSound(player, Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 1f, 1.2f)
                player.closeInventory()
            } else {
                val world = Bukkit.getWorld(split[3])
                val location = Location(world, split[0].toDouble(), split[1].toDouble(), split[2].toDouble())
                player.teleportAsync(location)
                player.sendMessage(msg("event.teleport", listOf("${location.blockX} ${location.blockY} ${location.blockZ}")))
            }
        }
    }

    //Constantly keep this listener running to prevent item glitching
    override fun register() {}
    override fun unregister() {}
}