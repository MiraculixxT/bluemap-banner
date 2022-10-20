package de.miraculixx.bluemap_marker.utils.interfaces

import net.axay.kspigot.event.SingleListener
import net.axay.kspigot.event.unregister
import org.bukkit.event.Event

interface MultiListener<T : Event> : Listener<T> {
    val sideListener: List<SingleListener<*>>

    override fun unregister() {
        listener.unregister()
    }
}