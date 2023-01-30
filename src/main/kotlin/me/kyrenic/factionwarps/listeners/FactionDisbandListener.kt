package me.kyrenic.factionwarps.listeners

import com.dansplugins.factionsystem.event.faction.FactionDisbandEvent
import me.kyrenic.factionwarps.FactionWarps
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.*

class FactionDisbandListener(private val plugin: FactionWarps) : Listener {

    @EventHandler
    fun onFactionDisband(event: FactionDisbandEvent) {
        plugin.services.warpService.deleteWarps(UUID.fromString(event.factionId.value))
    }
}