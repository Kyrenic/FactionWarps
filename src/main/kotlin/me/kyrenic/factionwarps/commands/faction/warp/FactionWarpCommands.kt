package me.kyrenic.factionwarps.commands.faction.warp

import me.kyrenic.factionwarps.FactionWarps
import me.kyrenic.factionwarps.warp.Warp
import org.bukkit.ChatColor
import org.bukkit.ChatColor.RED
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class FactionWarpCommands(private val plugin: FactionWarps) : CommandExecutor, TabCompleter {
    private val factionWarpWarpCommand = FactionWarpWarpCommand(plugin)
    private val factionWarpListCommand = FactionWarpListCommand(plugin)
    private val factionWarpCreateCommand = FactionWarpCreateCommand(plugin)
    private val factionWarpDeleteCommand = FactionWarpDeleteCommand(plugin)

    private val warpAliases = listOf(plugin.language["CommandWarpWarp"])
    private val listAliases = listOf(plugin.language["CommandWarpList"])
    private val createAliases = listOf(plugin.language["CommandWarpCreate"])
    private val deleteAliases = listOf(plugin.language["CommandWarpDelete"])

    private val subcommands = warpAliases + listAliases + createAliases + deleteAliases

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        return when (args.firstOrNull()?.lowercase()) {
            in listAliases -> factionWarpListCommand.onCommand(sender, command, label, args.drop(1).toTypedArray())
            in createAliases -> factionWarpCreateCommand.onCommand(sender, command, label, args.drop(1).toTypedArray())
            in deleteAliases -> factionWarpDeleteCommand.onCommand(sender, command, label, args.drop(1).toTypedArray())
            in warpAliases -> factionWarpWarpCommand.onCommand(sender, command, label, args.drop(1).toTypedArray())
            else -> {
                sender.sendMessage("$RED${plugin.language["CommandWarpUsage"]}")
                return true
            }
        }
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): List<String> {
        return subcommands.filter { it.startsWith(args[0].lowercase()) }
    }
}