package me.kyrenic.factionwarps.commands.faction.warp

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

class FactionWarpCreateCommand(private val plugin: FactionWarps) : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // Primary checks:
        // Sender needs to be a player.
        if (sender !is Player) {
            sender.sendMessage("${ChatColor.RED}${plugin.language["NotPlayer"]}")
            return true
        }
        // Sender needs to have the correct bukkit permission.
        if (!sender.hasPermission("fw.warp.create")) {
            sender.sendMessage("${ChatColor.RED}${plugin.language["NoPermission"]}")
            return true
        }
        // Command needs one argument.
        if (args.size !in 1..1) {
            sender.sendMessage("${ChatColor.RED}${plugin.language["CommandWarpCreateUsage"]}")
            return true
        }

        // All services and values needed in the function.
        val factionService = plugin.medievalFactions.services.factionService
        val factionPermissions = plugin.factionPermissions
        val claimService = plugin.medievalFactions.services.claimService
        val playerService = plugin.medievalFactions.services.playerService
        val warpService = plugin.services.warpService

        val senderMfPlayer = playerService.getPlayer(sender) ?: playerService.save(MfPlayer(plugin.medievalFactions, sender)).onFailure {
            sender.sendMessage("${ChatColor.RED}${plugin.language["FailedToSavePlayer"]}")
            plugin.logger.log(Level.SEVERE, "Failed to save player: ${it.reason.message}", it.reason.cause)
            return true
        }
        val senderFaction = factionService.getFaction(senderMfPlayer.id)
        if (senderFaction == null) {
            sender.sendMessage("${ChatColor.RED}${plugin.language["NotInFaction"]}")
            return true
        }

        // Secondary checks:
        // Sender needs to have the correct faction permission.
        val role = senderFaction.getRole(senderMfPlayer.id)
        if (role == null || !role.hasPermission(senderFaction, factionPermissions.warpCreate)) {
            sender.sendMessage("${ChatColor.RED}${plugin.language["NoFactionPermission"]}")
            return true
        }
        // Suggested warp must not go over the maximum warps.
        val maxWarpLimit = plugin.config.getInt("factionWarps.maxWarps")
        if (warpService.getWarps(UUID.fromString(senderFaction.id.value)).size > maxWarpLimit) {
            sender.sendMessage("${ChatColor.RED}${plugin.language["MaxWarpLimitReached", maxWarpLimit.toString()]}")
            return true
        }
        // Suggested warp needs to not exist.
        val warpName = args[0]
        val warp = warpService.getWarp(UUID.fromString(senderFaction.id.value), warpName)
        if (warp != null) {
            sender.sendMessage("${ChatColor.RED}${plugin.language["WarpAlreadyExists", warpName]}")
            return true
        }
        // Suggested warp needs to be in your own claim.
        val chunk = claimService.getClaim(sender.location.chunk)
        if (chunk?.factionId != senderFaction.id) {
            sender.sendMessage("${ChatColor.RED}${plugin.language["NotInYourClaim"]}")
            return true
        }

        // Actual command logic.
        warpService.saveWarp(Warp(UUID.randomUUID(), UUID.fromString(senderFaction.id.value), warpName, sender.location))
        sender.sendMessage("${ChatColor.GREEN}${plugin.language["WarpCreated", warpName]}")
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ) = emptyList<String>()
}