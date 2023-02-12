package me.kyrenic.factionwarps.services

import me.kyrenic.factionwarps.jooq.JooqLogService
import org.bukkit.plugin.Plugin
import java.util.*

class LogService(private val plugin: Plugin, private val jooq: JooqLogService) {
    fun save(senderId: UUID?, command: String) = jooq.save(senderId, command)

    fun getEntry(senderId: UUID?) = jooq.getEntry(senderId)

    fun getEntryContaining(senderId: UUID?, vararg strings: String) = jooq.getEntryContaining(senderId, *strings)
}