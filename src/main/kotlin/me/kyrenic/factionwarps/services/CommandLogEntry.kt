package me.kyrenic.factionwarps.services

import java.time.LocalDateTime

data class CommandLogEntry(
    val id: Int,
    val senderId: String,
    val command: String,
    val time: LocalDateTime
)