package me.kyrenic.factionwarps.jooq

import me.kyrenic.factionwarps.jooq.tables.records.WarpBannedFactionsRecord
import me.kyrenic.factionwarps.jooq.tables.records.WarpBannedPlayersRecord
import me.kyrenic.factionwarps.jooq.tables.records.WarpsRecord
import me.kyrenic.factionwarps.jooq.Tables.WARPS
import me.kyrenic.factionwarps.jooq.Tables.WARP_BANNED_FACTIONS
import me.kyrenic.factionwarps.jooq.Tables.WARP_BANNED_PLAYERS
import me.kyrenic.factionwarps.warp.Warp
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.plugin.Plugin
import org.jooq.DSLContext
import org.jooq.impl.DSL.floor
import java.util.UUID


class JooqWarpService(private val plugin: Plugin, private val dsl: DSLContext) {

    fun getWarp(warpId: UUID): Warp? = dsl.selectFrom(WARPS)
        .where(WARPS.ID.eq(warpId.toString()))
        .fetchOne()
        ?.toDomain()

    fun getWarp(factionId: UUID, name: String): Warp? = dsl.selectFrom(WARPS)
        .where(WARPS.FACTION_ID.eq(factionId.toString()))
        .and(WARPS.NAME.eq(name))
        .fetchOne()
        ?.toDomain()

    fun getWarpsAtChunk(factionId: UUID, chunk: Chunk): List<Warp> = dsl.selectFrom(WARPS)
        .where(WARPS.FACTION_ID.eq(factionId.toString()))
        .and(floor(WARPS.X.div(16)).eq(chunk.x.toDouble()))
        .and(floor(WARPS.Z.div(16)).eq(chunk.z.toDouble()))
        .fetch()
        .map { it.toDomain() }

    fun getWarps(factionId: UUID): List<Warp> =dsl.selectFrom(WARPS)
        .where(WARPS.FACTION_ID.eq(factionId.toString()))
        .fetch()
        .map { it.toDomain() }

    fun saveWarp(warp: Warp) {
        dsl.insertInto(WARPS)
            .set(WARPS.ID, warp.id.toString())
            .set(WARPS.FACTION_ID, warp.factionId.toString())
            .set(WARPS.NAME, warp.name)
            .set(WARPS.ACCESSIBLE, warp.accessible)
            .set(WARPS.TAX, warp.tax)
            .set(WARPS.WORLD, requireNotNull(warp.location.world).uid.toString())
            .set(WARPS.X, warp.location.x)
            .set(WARPS.Y, warp.location.y)
            .set(WARPS.Z, warp.location.z)
            .set(WARPS.YAW, warp.location.yaw)
            .set(WARPS.PITCH, warp.location.pitch)
            .onConflict(WARPS.ID).doUpdate()
            .set(WARPS.NAME, warp.name)
            .set(WARPS.ACCESSIBLE, warp.accessible)
            .set(WARPS.TAX, warp.tax)
            .set(WARPS.WORLD, requireNotNull(warp.location.world).uid.toString())
            .set(WARPS.X, warp.location.x)
            .set(WARPS.Y, warp.location.y)
            .set(WARPS.Z, warp.location.z)
            .set(WARPS.YAW, warp.location.yaw)
            .set(WARPS.PITCH, warp.location.pitch)
            .where(WARPS.ID.eq(warp.id.toString()))
            .execute()
    }

    fun deleteWarp(warpId: UUID) = dsl.deleteFrom(WARPS)
        .where(WARPS.ID.eq(warpId.toString()))
        .execute()

    fun deleteWarps(factionId: UUID) = dsl.deleteFrom(WARPS)
        .where(WARPS.FACTION_ID.eq(factionId.toString()))
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

    private fun getBannedFactions(warpId: String): List<UUID> = dsl.selectFrom(WARP_BANNED_FACTIONS)
        .where(WARP_BANNED_FACTIONS.WARP_ID.eq(warpId))
        .fetch()
        .toDomain()

    private fun getBannedPlayers(warpId: String): List<UUID> = dsl.selectFrom(WARP_BANNED_PLAYERS)
        .where(WARP_BANNED_PLAYERS.WARP_ID.eq(warpId))
        .fetch()
        .toDomain()

    private fun WarpsRecord.toDomain(): Warp {
        val bannedFactions = getBannedFactions(requireNotNull(id))
        val bannedPlayers = getBannedPlayers(requireNotNull(id))
        val warpLocation = Location(
            plugin.server.getWorld(UUID.fromString(world)),
            requireNotNull(x),
            requireNotNull(y),
            requireNotNull(z),
            requireNotNull(yaw),
            requireNotNull(pitch)
        )

        return Warp(
            UUID.fromString(id),
            UUID.fromString(factionId),
            requireNotNull(name),
            warpLocation,
            requireNotNull(accessible),
            requireNotNull(tax),
            bannedFactions,
            bannedPlayers
        )
    }

    @JvmName("bannedPlayersRecordToDomain")
    private fun List<WarpBannedPlayersRecord>.toDomain(): List<UUID> = map { UUID.fromString(it.bannedPlayerId) }

    @JvmName("bannedFactionsRecordToDomain")
    private fun List<WarpBannedFactionsRecord>.toDomain(): List<UUID> = map { UUID.fromString(it.bannedFactionId) }
}