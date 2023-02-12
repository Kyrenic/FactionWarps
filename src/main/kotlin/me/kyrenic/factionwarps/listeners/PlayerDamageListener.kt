package me.kyrenic.factionwarps.listeners

import me.kyrenic.factionwarps.FactionWarps
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent

class PlayerDamageListener(private val plugin: FactionWarps) : Listener {

    @EventHandler
    fun onPlayerDamage(event: EntityDamageEvent) {

    }
}