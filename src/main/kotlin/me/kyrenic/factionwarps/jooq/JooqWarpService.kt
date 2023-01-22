package me.kyrenic.factionwarps.jooq

import me.kyrenic.factionwarps.jooq.tables.records.WarpsRecord
import me.kyrenic.factionwarps.jooq.tables.references.WARPS
import me.kyrenic.factionwarps.jooq.tables.references.WARP_BANNED_FACTIONS
import me.kyrenic.factionwarps.jooq.tables.references.WARP_BANNED_PLAYERS
import me.kyrenic.factionwarps.jooq.tables.references.WARP_LOCATIONS
import me.kyrenic.factionwarps.warp.Warp
import org.bukkit.Location
import org.bukkit.plugin.Plugin
import org.jooq.DSLContext
import java.util.UUID


class JooqWarpService(private val dsl: DSLContext, private val plugin: Plugin) {

    private fun buildWarp(warpResult: WarpsRecord): Warp {
        val warpLocationResult = dsl.selectFrom(WARP_LOCATIONS)
            .where(WARPS.ID.eq(warpResult.id))
            .fetchOne()

        val bannedFactions = getBannedFactions(warpResult.id!!)
        val bannedPlayers = getBannedPlayers(warpResult.id!!)
        val warpLocation = Location(
            plugin.server.getWorld(UUID.fromString(warpLocationResult?.world)),
            warpLocationResult?.x!!,
            warpLocationResult.y!!,
            warpLocationResult.z!!,
            warpLocationResult.yaw!!,
            warpLocationResult.pitch!!
        )

        return Warp(
            UUID.fromString(warpResult.id!!),
            UUID.fromString(warpResult.factionId!!),
            warpResult.name!!,
            warpLocation,
            warpResult.accessible!!,
            warpResult.tax!!,
            bannedFactions,
            bannedPlayers
        )
    }

    fun getWarp(warpId: UUID): Warp? {
        val warpResult = dsl.selectFrom(WARPS)
            .where(WARPS.ID.eq(warpId.toString()))
            .fetchOne() ?: return null
        return buildWarp(warpResult)
    }

    fun getWarp(factionId: UUID, name: String): Warp? {
        val warpResult = dsl.selectFrom(WARPS)
            .where(WARPS.FACTION_ID.eq(factionId.toString()))
            .and(WARPS.NAME.eq(name))
            .fetchOne() ?: return null
        return buildWarp(warpResult)
    }

    fun getWarps(factionId: UUID): List<Warp> {
        val warpResults = dsl.selectFrom(WARPS)
            .where(WARPS.FACTION_ID.eq(factionId.toString()))
            .fetch()
        val warpList: MutableList<Warp> = mutableListOf()
        warpResults.forEach {
            warpList.add(buildWarp(it))
        }
        return warpList.toList()
    }

    fun saveWarp(warp: Warp) {
        dsl.insertInto(WARPS)
            .set(WARPS.ID, warp.id.toString())
            .set(WARPS.FACTION_ID, warp.factionId.toString())
            .set(WARPS.ACCESSIBLE, warp.accessible)
            .set(WARPS.TAX, warp.tax)
            .onConflict(WARPS.ID).doUpdate()
            .set(WARPS.ACCESSIBLE, warp.accessible)
            .set(WARPS.TAX, warp.tax)
            .where(WARPS.ID.eq(warp.id.toString()))
            .execute()
        val location = warp.location
        dsl.insertInto(WARP_LOCATIONS)
            .set(WARP_LOCATIONS.WARP_ID, warp.id.toString())
            .set(WARP_LOCATIONS.WORLD, location.world.toString())
            .set(WARP_LOCATIONS.X, location.x)
            .set(WARP_LOCATIONS.Y, location.y)
            .set(WARP_LOCATIONS.Z, location.z)
            .set(WARP_LOCATIONS.YAW, location.yaw)
            .set(WARP_LOCATIONS.PITCH, location.pitch)
            .onConflict(WARP_LOCATIONS.WARP_ID).doUpdate()
            .set(WARP_LOCATIONS.WORLD, location.world.toString())
            .set(WARP_LOCATIONS.X, location.x)
            .set(WARP_LOCATIONS.Y, location.y)
            .set(WARP_LOCATIONS.Z, location.z)
            .set(WARP_LOCATIONS.YAW, location.yaw)
            .set(WARP_LOCATIONS.PITCH, location.pitch)
            .where(WARP_LOCATIONS.WARP_ID.eq(warp.id.toString()))
            .execute()
    }

    fun deleteWarp(warpId: UUID) = dsl.deleteFrom(WARPS)
        .where(WARPS.ID.eq(warpId.toString()))
        .execute()

    fun isFactionBanned(warp: Warp, factionId: UUID): Boolean {
        val result = dsl.selectFrom(WARP_BANNED_FACTIONS)
            .where(WARP_BANNED_FACTIONS.WARP_ID.eq(warp.id.toString()))
            .and(WARP_BANNED_FACTIONS.BANNED_FACTION_ID.eq(factionId.toString()))
            .fetchOne()
        return result != null
    }


    fun isPlayerBanned(warp: Warp, playerId: UUID): Boolean {
        val result = dsl.selectFrom(WARP_BANNED_PLAYERS)
            .where(WARP_BANNED_PLAYERS.WARP_ID.eq(warp.id.toString()))
            .and(WARP_BANNED_PLAYERS.BANNED_PLAYER_ID.eq(playerId.toString()))
            .fetchOne()
        return result != null
    }


    private fun getBannedFactions(warpId: String): List<UUID> {
        val bannedFactionList = mutableListOf<UUID>()
        val result = dsl.selectFrom(WARP_BANNED_FACTIONS)
            .where(WARP_BANNED_FACTIONS.WARP_ID.eq(warpId))
            .fetch()
        result.forEach {
            bannedFactionList.add(UUID.fromString(it.bannedFactionId))
        }
        return bannedFactionList.toList()
    }

    private fun getBannedPlayers(warpId: String): List<UUID> {
        val bannedPlayerList = mutableListOf<UUID>()
        val result = dsl.selectFrom(WARP_BANNED_PLAYERS)
            .where(WARP_BANNED_PLAYERS.WARP_ID.eq(warpId))
            .fetch()
        result.forEach {
            bannedPlayerList.add(UUID.fromString(it.bannedPlayerId))
        }
        return bannedPlayerList.toList()
    }
}