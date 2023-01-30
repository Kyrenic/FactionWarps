package me.kyrenic.factionwarps.commands.faction.warp

import com.dansplugins.factionsystem.faction.MfFaction
import com.dansplugins.factionsystem.faction.MfFactionId
import com.dansplugins.factionsystem.player.MfPlayer
import dev.forkhandles.result4k.onFailure
import me.kyrenic.factionwarps.FactionWarps
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import java.util.*
import java.util.logging.Level
import net.md_5.bungee.api.ChatColor as SpigotChatColor
import org.bukkit.ChatColor as BukkitChatColor

class FactionWarpListCommand(private val plugin: FactionWarps) : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // Primary checks:
        // Sender needs to be a player.
        if (sender !is Player) {
            sender.sendMessage("${BukkitChatColor.RED}${plugin.language["NotPlayer"]}")
            return true
        }
        // Sender needs to have the correct bukkit permission.
        if (!sender.hasPermission("fw.warp.list")) {
            sender.sendMessage("${BukkitChatColor.RED}${plugin.language["NoPermission"]}")
            return true
        }
        // Command needs no or one argument.
        if (args.size !in 0..1) {
            sender.sendMessage("${BukkitChatColor.RED}${plugin.language["CommandWarpListUsage"]}")
            return true
        }

        // All services and values needed in the function.
        val factionService = plugin.medievalFactions.services.factionService
        val factionPermissions = plugin.factionPermissions
        val playerService = plugin.medievalFactions.services.playerService
        val warpService = plugin.services.warpService

        val senderMfPlayer = playerService.getPlayer(sender) ?: playerService.save(MfPlayer(plugin.medievalFactions, sender)).onFailure {
            sender.sendMessage("${BukkitChatColor.RED}${plugin.language["FailedToSavePlayer"]}")
            plugin.logger.log(Level.SEVERE, "Failed to save player: ${it.reason.message}", it.reason.cause)
            return true
        }
        val faction: MfFaction?
        if (args.size == 1) {
            faction = factionService.getFaction(args[0])
            if (faction == null) {
                sender.sendMessage("${BukkitChatColor.RED}${plugin.language["NotAFaction"]}")
                return true
            }
        } else {
            faction = factionService.getFaction(senderMfPlayer.id)
            if (faction == null) {
                sender.sendMessage("${BukkitChatColor.RED}${plugin.language["NotInFaction"]}")
                return true
            }
        }
        val role = faction.getRole(senderMfPlayer.id)
        if (role == null || !role.hasPermission(faction, factionPermissions.warpList)) {
            sender.sendMessage("${org.bukkit.ChatColor.RED}${plugin.language["NoFactionPermission"]}")
            return true
        }

        // Actual command logic:
        val warps = warpService.getWarps(UUID.fromString(faction.id.value))
        sender.sendMessage(plugin.language["WarpListHeading"])
        warps.forEachIndexed { i, warp ->
            val buttonList: MutableList<TextComponent> = mutableListOf()
            if (sender.hasPermission("fw.warp.delete") && role.hasPermission(faction, factionPermissions.warpDelete)) {
                val deleteButton = TextComponent("✖ ")
                deleteButton.color = SpigotChatColor.RED
                deleteButton.clickEvent = ClickEvent(RUN_COMMAND, "/fwarp delete ${warp.name}")
                deleteButton.hoverEvent =
                    HoverEvent(SHOW_TEXT, Text(plugin.language["WarpListEntryDeleteButtonHover"]))
                buttonList.add(deleteButton)
            }
            if (sender.hasPermission("fw.warp.edit") && role.hasPermission(faction, factionPermissions.warpEdit)) {
                val editButton = TextComponent("✎ ")
                editButton.color = SpigotChatColor.YELLOW
                editButton.clickEvent = ClickEvent(RUN_COMMAND, "/fwarp edit ${warp.name}")
                editButton.hoverEvent =
                    HoverEvent(SHOW_TEXT, Text(plugin.language["WarpListEntryEditButtonHover"]))
                buttonList.add(editButton)
            }
            val infoList: MutableList<TextComponent> = mutableListOf()
            if (warp.tax > 0) {
                infoList.add(TextComponent(plugin.language["WarpListEntryInfoTax", warp.tax.toString()]))
            }
            when (warp.accessible) {
                true -> infoList.add(TextComponent(plugin.language["WarpListEntryInfoOpenClosed", plugin.language["WarpListEntryInfoOpen"]]))
                false -> infoList.add(TextComponent(plugin.language["WarpListEntryInfoOpenClosed", plugin.language["WarpListEntryInfoClosed"]]))
            }
            val text = TextComponent(plugin.language["WarpListEntry", warp.name])
            sender.spigot().sendMessage(
                *buttonList.toTypedArray(),
                text,
                *infoList.toTypedArray()
            )
        }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): List<String> {
        return if (args.size == 1) {
            plugin.medievalFactions.services.factionService.factions.filter { it.name.startsWith(args[0].lowercase()) }.map(MfFaction::name)
        } else {
            listOf()
        }
    }
}