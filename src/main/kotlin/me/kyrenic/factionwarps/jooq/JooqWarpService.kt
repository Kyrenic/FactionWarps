package me.kyrenic.factionwarps.jooq

import com.dansplugins.factionsystem.faction.MfFactionId
import com.dansplugins.factionsystem.jooq.Tables
import me.kyrenic.factionwarps.warp.Warp
import org.jooq.DSLContext
import java.util.UUID

class JooqWarpService(private val dsl: DSLContext) {
    fun getWarp(warpId: UUID): Warp? =
        dsl.selectFrom(Tables.warps)
            .where(Tables.warps.id.eq(warpId))
            .fetchOne()
            ?.toDomain()

    fun getWarp(factionId: UUID, name: String): Warp? =
        dsl.selectFrom(Tables.warps)
            .where(Tables.warps.faction_id.eq(factionId))
            .fetchOne()
            ?.toDomain()

    fun getWarps(factionId: UUID): List<Warp> =
        dsl.selectFrom(Tables.warps)
            .where(Tables.warps.faction_id.eq(factionId.value))
            .fetch()
            .map { it.toDomain() }

    fun saveWarp(warp: Warp) {

    }

    fun deleteWarp(warpId: UUID) = dsl.deleteFrom(Tables.MF_LAW)
        .where(Tables.warps.id.eq(warpId))
        .execute()

    fun isFactionBanned(warp: Warp, factionId: UUID): Boolean =
        dsl.selectFrom(Tables.warp_banned_factions)
            .where(Tables.warp_banned_factions.warp_id.eq(warp.id))
            .and(Tables.warp_banned_factions.banned_faction_id.eq(factionId))
            .fetchOne()

    fun isPlayerBanned(warp: Warp, playerId: UUID): Boolean =
        dsl.selectFrom(Tables.warp_banned_players)
            .where(Tables.warp_banned_players.warp_id.eq(warp.id))
            .and(Tables.warp_banned_players.banned_player_id.eq(playerId))
            .fetchOne()
}