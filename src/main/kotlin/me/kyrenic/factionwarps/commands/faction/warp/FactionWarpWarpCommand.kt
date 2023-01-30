package me.kyrenic.factionwarps.commands.faction.warp

import com.dansplugins.factionsystem.faction.MfFactionId
import com.dansplugins.factionsystem.player.MfPlayer
import dev.forkhandles.result4k.onFailure
import me.kyrenic.factionwarps.FactionWarps
import me.kyrenic.factionwarps.warp.Warp
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import java.util.*
import java.util.logging.Level

class FactionWarpWarpCommand(private val plugin: FactionWarps) : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // Primary checks:
        // Sender needs to be a player.
        if (sender !is Player) {
            sender.sendMessage("${ChatColor.RED}${plugin.language["NotPlayer"]}")
            return true
        }
        // Sender needs to have the correct bukkit permission.
        if (!sender.hasPermission("fw.warp.warp")) {
            sender.sendMessage("${ChatColor.RED}${plugin.language["NoPermission"]}")
            return true
        }
        // Command needs no argument.
        if (args.size != 2) {
            sender.sendMessage("${ChatColor.RED}${plugin.language["CommandWarpWarpUsage"]}")
            return true
        }

        // All services and values needed in the function.
        val factionService = plugin.medievalFactions.services.factionService
        val factionPermissions = plugin.factionPermissions
        val playerService = plugin.medievalFactions.services.playerService
        val warpService = plugin.services.warpService

        val senderMfPlayer = playerService.getPlayer(sender) ?: playerService.save(MfPlayer(plugin.medievalFactions, sender)).onFailure {
            sender.sendMessage("${ChatColor.RED}${plugin.language["FailedToSavePlayer"]}")
            plugin.logger.log(Level.SEVERE, "Failed to save player: ${it.reason.message}", it.reason.cause)
            return true
        }
        val senderFaction = factionService.getFaction(senderMfPlayer.id)
        val argumentFaction = factionService.getFaction(args[0])

        // Secondary checks:
        // Faction needs to exist.
        if (argumentFaction == null) {
            sender.sendMessage("${ChatColor.RED}${plugin.language["NotAFaction"]}")
            return true
        }
        // Warp needs to exist.
        val warpName = args[1]
        val warp = warpService.getWarp(UUID.fromString(argumentFaction.id.value), warpName)
        if (warp == null) {
            sender.sendMessage("${ChatColor.RED}${plugin.language["WarpDoesNotExist", warpName]}")
            return true
        }
        // Sender needs to have the correct faction permission to warp to their own factions warps.
        val role = senderFaction?.getRole(senderMfPlayer.id)
        if (role != null && UUID.fromString(senderFaction.id.value) == warp.factionId && !role.hasPermission(senderFaction, factionPermissions.warp)) {
            sender.sendMessage("${ChatColor.RED}${plugin.language["NoFactionPermission"]}")
            return true
        }
        // Sender needs to have the correct faction permission to warp to other factions warps.
        if (role != null && UUID.fromString(senderFaction.id.value) != warp.factionId && !role.hasPermission(senderFaction, factionPermissions.warpOthers)) {
            sender.sendMessage("${ChatColor.RED}${plugin.language["NoFactionPermission"]}")
            return true
        }
        // Warp needs to be open.
        if (UUID.fromString(senderFaction?.id?.value) != warp.factionId && !warp.accessible) {
            sender.sendMessage("${ChatColor.RED}${plugin.language["WarpNotOpen"]}")
            return true
        }
        // You cannot be banned.
        if (warpService.isPlayerBanned(warp, sender.uniqueId)) {
            sender.sendMessage("${ChatColor.RED}${plugin.language["WarpBannedPlayer", factionService.getFaction(MfFactionId(warp.factionId.toString())).toString()]}")
            return true
        }
        // Your faction cannot be banned.
        if (senderFaction != null && warpService.isFactionBanned(warp, UUID.fromString(senderFaction.id.value))) {
            sender.sendMessage("${ChatColor.RED}${plugin.language["WarpBannedFaction", factionService.getFaction(MfFactionId(warp.factionId.toString())).toString()]}")
            return true
        }

        // Actual command logic.
        warpService.warp(sender, warp.location)
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): List<String> {
        if (args.size == 1) {
            val playerId = plugin.medievalFactions.services.playerService.getPlayer(sender as Player)?.id ?: return emptyList()
            val factionId = plugin.medievalFactions.services.factionService.getFaction(playerId)?.id ?: return emptyList()
            val warpList = plugin.services.warpService.getWarps(UUID.fromString(factionId.value))
            return warpList.filter { it.name.startsWith(args[0].lowercase()) }.map(Warp::name)
        } else {
            return emptyList()
        }
    }
}