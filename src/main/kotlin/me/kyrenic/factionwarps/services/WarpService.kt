package me.kyrenic.factionwarps.services

import me.kyrenic.factionwarps.jooq.JooqWarpService
import me.kyrenic.factionwarps.warp.Warp
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.UUID

class WarpService(private val plugin: Plugin, private val jooq: JooqWarpService) {
    fun getWarp(warpId: UUID): Warp? = jooq.getWarp(warpId)

    fun getWarp(factionId: UUID, name: String): Warp? = jooq.getWarp(factionId, name)

    fun getWarpsAtChunk(factionId: UUID, chunk: Chunk) = jooq.getWarpsAtChunk(factionId, chunk)

    fun getWarps(factionId: UUID): List<Warp> = jooq.getWarps(factionId)

    fun saveWarp(warp: Warp) = jooq.saveWarp(warp)

    fun deleteWarp(warpId: UUID) = jooq.deleteWarp(warpId)

    fun deleteWarp(warp: Warp) = deleteWarp(warp.id)

    fun deleteWarps(factionId: UUID) = jooq.deleteWarps(factionId)

    fun deleteWarp(factionId: UUID, name: String) {
        val warp = getWarp(factionId, name) ?: return
        deleteWarp(warp)
    }

    fun isFactionBanned(warp: Warp, factionId: UUID): Boolean = jooq.isFactionBanned(warp, factionId)

    fun isPlayerBanned(warp: Warp, playerId: UUID): Boolean = jooq.isPlayerBanned(warp, playerId)
}
