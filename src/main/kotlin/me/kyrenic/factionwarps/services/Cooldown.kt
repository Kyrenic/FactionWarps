package me.kyrenic.factionwarps.services

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

data class Cooldown(
    val time: LocalDateTime,
    val duration: Int,
    val chronoUnit: ChronoUnit
) {
    fun onCooldown(): Boolean = timePassed() <= duration

    fun timePassed(): Int = time.until(LocalDateTime.now(), chronoUnit).toInt()
}