package me.kyrenic.factionwarps.jooq

import me.kyrenic.factionwarps.jooq.Tables.COMMAND_LOG
import me.kyrenic.factionwarps.jooq.tables.records.CommandLogRecord
import me.kyrenic.factionwarps.services.CommandLogEntry
import org.bukkit.plugin.Plugin
import org.jooq.DSLContext
import java.util.*

class JooqLogService(private val plugin: Plugin, private val dsl: DSLContext) {

    fun save(senderId: UUID?, command: String) = dsl.insertInto(COMMAND_LOG)
        .set(COMMAND_LOG.SENDER_ID, senderId.toString())
        .set(COMMAND_LOG.COMMAND, command)
        .execute()

    fun getEntry(senderId: UUID?): List<CommandLogEntry> = dsl.selectFrom(COMMAND_LOG)
        .where(COMMAND_LOG.SENDER_ID.eq(senderId.toString()))
        .orderBy(COMMAND_LOG.TIME.desc())
        .fetch()
        .toDomain()

    fun getEntryContaining(senderId: UUID?, vararg strings: String): List<CommandLogEntry> = dsl.selectFrom(COMMAND_LOG)
        .where(COMMAND_LOG.SENDER_ID.eq(senderId.toString()))
        .apply { strings.forEach { and(COMMAND_LOG.COMMAND.contains(it)) } }
        .orderBy(COMMAND_LOG.TIME.desc())
        .fetch()
        .toDomain()

    private fun List<CommandLogRecord>.toDomain() = map { CommandLogEntry(
        it.id,
        it.senderId,
        it.command,
        it.time
    ) }
}