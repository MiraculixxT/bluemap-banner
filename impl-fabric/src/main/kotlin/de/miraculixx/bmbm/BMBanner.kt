package de.miraculixx.bmbm

import de.miraculixx.bmbm.events.blockPlaceListener
import de.miraculixx.bmbm.utils.cHighlight
import de.miraculixx.bmbm.utils.cmp
import de.miraculixx.bmbm.utils.plus
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.platform.fabric.FabricServerAudiences
import net.kyori.adventure.text.format.NamedTextColor
import net.minecraft.server.MinecraftServer
import net.silkmc.silk.core.event.Events
import net.silkmc.silk.core.event.Server

lateinit var server: MinecraftServer
lateinit var consoleAudience: Audience
lateinit var adventure: FabricServerAudiences
val prefix = cmp("MUtils", cHighlight) + cmp(" >>", NamedTextColor.DARK_GRAY) + cmp(" ")

fun init() {

    Events.Server.postStart.listen { event ->
        adventure = FabricServerAudiences.of(event.server)
        consoleAudience = adventure.console()
        println("Server start")
    }

    UseBlockCallback.EVENT.register(blockPlaceListener)
}