package me.kyrenic.factionwarps.services

import me.kyrenic.factionwarps.FactionWarps
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import java.util.UUID

class TeleportService(private val plugin: FactionWarps) {
    private val warpTeleports = mutableMapOf<UUID, BukkitTask>()

    fun warpTeleport(player: Player, location: Location) {
        val teleportDelay = plugin.config.getInt("factionWarps.delay")
        if (teleportDelay <= 0) {
            executeWarpTeleport(player, location)
        }
        val uuid = player.uniqueId
        warpTeleports[uuid]?.cancel()
        player.sendMessage("${ChatColor.GRAY}${plugin.language["Teleporting"]}")
        val task = plugin.server.scheduler.runTaskLater(
            plugin,
            Runnable {
                warpTeleports.remove(uuid)
                val playerToTeleport = plugin.server.getPlayer(uuid)
                if (playerToTeleport != null) {
                    executeWarpTeleport(player, location)
                }
            },
            teleportDelay * 20L
        )
        warpTeleports[uuid] = task
    }

    fun executeWarpTeleport(player: Player, location: Location) {
        player.teleport(location)
        val soundName = plugin.config.getString("factionWarps.teleportSound") ?: return
        player.playSound(player, Sound.valueOf(soundName), 1f, 1f)
    }

    fun cancelTeleport(player: Player) {
        cancelWarpTeleport(player)
    }

    fun cancelWarpTeleport(player: Player) {
        val warpTeleport = warpTeleports[player.uniqueId] ?: return
        warpTeleport.cancel()
        warpTeleports.remove(player.uniqueId)
        player.sendMessage("${ChatColor.RED}${plugin.language["TeleportCancelled"]}")
    }
}