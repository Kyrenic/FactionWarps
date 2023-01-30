package me.kyrenic.factionwarps.listeners

import com.dansplugins.factionsystem.event.faction.FactionCreateEvent
import com.dansplugins.factionsystem.faction.role.MfFactionRole
import me.kyrenic.factionwarps.FactionWarps
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class FactionCreateListener(private val plugin: FactionWarps) : Listener {

    @EventHandler
    fun onFactionCreate(event: FactionCreateEvent) {
        // Add permissions.
        val factionPermissions = plugin.factionPermissions
        // Loop through all the factions.
        val newDefaultPermissionsByName = event.faction.defaultPermissionsByName.toMutableMap()
        var newRoles: List<MfFactionRole> = event.faction.roles.copy()
        // Loop through all the permissions that this extension adds.
        factionPermissions.permissionList.forEach { permission ->
            // Check if the permission is present in the list, if not add it.
            // Add it to the high-ranking roles:
            newRoles = newRoles.map { role ->
                // Build a new permissions map.
                val permissionsToAdd = buildMap {
                    if (role.hasPermission(event.faction, plugin.medievalFactions.factionPermissions.changeName) && !permission.default) {
                        put(permission.name, true)
                    }
                }
                // Add the map to the existing map.
                role.copy(permissionsByName = role.permissionsByName + permissionsToAdd)
            }
        }
        event.faction = event.faction.copy(defaultPermissionsByName = newDefaultPermissionsByName, roles = event.faction.roles.copy(roles = newRoles))
    }
}