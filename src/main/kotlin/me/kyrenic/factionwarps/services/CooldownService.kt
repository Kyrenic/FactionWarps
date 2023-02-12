package me.kyrenic.factionwarps.services

import me.kyrenic.factionwarps.FactionWarps
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.time.temporal.ChronoUnit

class CooldownService(private val plugin: FactionWarps) {
    fun cooldown(sender: CommandSender, command: Command, args: Array<out String>): Cooldown? {
        val cooldown: Cooldown
        val key = "${command.name} ${args.joinToString(" ")}"
        val id = if (sender is Player) { sender.uniqueId } else { null }

        for (i in 0..args.size) {
            val altKey = "${command.name} ${args.dropLast(i).joinToString(" ")}"
            if (plugin.config.get("factionWarps.cooldown.${altKey}", null) != null) {
                val entries = plugin.services.logService.getEntryContaining(id, altKey)
                val time = entries.firstOrNull()?.time ?: break
                val duration = plugin.config.getInt("factionWarps.cooldown.${altKey}.duration")
                val chronoUnit = ChronoUnit.valueOf(plugin.config.getString("factionWarps.cooldown.${altKey}.unit") ?: "SECONDS")
                cooldown = Cooldown(
                    time,
                    duration,
                    chronoUnit
                )
                if (!cooldown.onCooldown()) {
                    saveInLog(sender, key)
                }
                return cooldown
            }
        }
        saveInLog(sender, key)
        return null
    }

    fun saveInLog(sender: CommandSender, command: String) {
        val id = if (sender is Player) { sender.uniqueId } else { null }
        plugin.services.logService.save(id, command)
    }
}
