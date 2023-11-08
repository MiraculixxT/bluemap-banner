package de.miraculixx.bmbm.utils

import de.miraculixx.bmbm.utils.messages.cMark
import de.miraculixx.bmbm.utils.messages.prefix
import de.miraculixx.kpaper.extensions.bukkit.cmp
import de.miraculixx.kpaper.extensions.bukkit.plus
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

object GlobalListener: Listener {
    @EventHandler
    fun playerJoinEvent(it: PlayerJoinEvent) {
        val player = it.player
        if (APIConnector.isOutdated && player.hasPermission("bmbm.updater")) {
            player.sendMessage(prefix + cmp("You are running an outdated version of BM-Banner!"))
            player.sendMessage(prefix + APIConnector.outdatedMessage)
            player.sendMessage(prefix + cmp("Click ") + cmp("here", cMark).clickEvent(ClickEvent.openUrl("https://modrinth.com/mod/bbanner")) + cmp(" to install the newest version."))
        }
    }
}