package me.kyrenic.factionwarps.warp

import com.dansplugins.factionsystem.faction.MfFactionId
import org.bukkit.Location
import java.util.UUID

data class Warp(
    val id: UUID,
    val factionId: UUID,
    val name: String,
    val location: Location,
    val accessible: Boolean = false,
    val tax: Double = 0.0,
    val bannedFactionIds: List<UUID> = emptyList(),
    val bannedPlayerIds: List<UUID> = emptyList()
)