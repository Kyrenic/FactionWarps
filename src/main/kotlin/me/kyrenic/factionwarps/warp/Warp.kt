package me.kyrenic.factionwarps.warp

import com.dansplugins.factionsystem.faction.MfFactionId
import org.bukkit.Location
import java.util.UUID

data class Warp(
    val id: UUID,
    val factionId: MfFactionId,
    val name: String,
    val location: Location,
    val open: Boolean = false,
    val tax: Double = 0.0,
    val bannedFactionIds: List<MfFactionId> = emptyList(),
    val bannedPlayerIds: List<UUID> = emptyList()
)