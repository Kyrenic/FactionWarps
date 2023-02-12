package me.kyrenic.factionwarps.listeners

import me.kyrenic.factionwarps.FactionWarps
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerQuitEvent

class PlayerQuitListener(private val plugin: FactionWarps) {

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {

    }
}