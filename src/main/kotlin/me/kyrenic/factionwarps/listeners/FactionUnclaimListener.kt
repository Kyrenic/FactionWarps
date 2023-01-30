package me.kyrenic.factionwarps.listeners

import com.dansplugins.factionsystem.event.faction.FactionUnclaimEvent
import me.kyrenic.factionwarps.FactionWarps
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.UUID

class FactionUnclaimListener(private val plugin: FactionWarps) : Listener {

    @EventHandler
    fun onFactionUnclaim(event: FactionUnclaimEvent) {
        val warpService = plugin.services.warpService
        val chunk = plugin.server.getWorld(event.claim.worldId)?.getChunkAt(event.claim.x, event.claim.z) ?: return
        val warpsInChunk = warpService.getWarpsAtChunk(UUID.fromString(event.factionId.value), chunk)
        warpsInChunk.forEach {
            warpService.deleteWarp(it)
        }
    }
}