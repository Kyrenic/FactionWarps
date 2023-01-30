package me.kyrenic.factionwarps.listeners

import com.dansplugins.factionsystem.event.faction.FactionCreateEvent
import me.kyrenic.factionwarps.FactionWarps
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class FactionCreateListener(private val plugin: FactionWarps) : Listener {

    @EventHandler
    fun onFactionCreate(event: FactionCreateEvent) {

    }
}