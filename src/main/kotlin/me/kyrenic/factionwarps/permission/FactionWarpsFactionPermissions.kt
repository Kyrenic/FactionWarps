package me.kyrenic.factionwarps.permission

import me.kyrenic.factionwarps.FactionWarps

class FactionWarpsFactionPermissions(private val plugin: FactionWarps) {

    private val permissions = plugin.medievalFactions.factionPermissions

    init {
        permissions.addPermissionType(permissions.wrapSimplePermission("WARP", plugin.language["FactionPermissionWarp"], true))
        permissions.addPermissionType(permissions.wrapSimplePermission("WARP_OTHERS", plugin.language["FactionPermissionWarp"], true))
        permissions.addPermissionType(permissions.wrapSimplePermission("WARP_LIST", plugin.language["FactionPermissionWarpList"], true))
        permissions.addPermissionType(permissions.wrapSimplePermission("WARP_CREATE", plugin.language["FactionPermissionWarpCreate"], false))
        permissions.addPermissionType(permissions.wrapSimplePermission("WARP_DELETE", plugin.language["FactionPermissionWarpDelete"], false))
        permissions.addPermissionType(permissions.wrapSimplePermission("WARP_EDIT", plugin.language["FactionPermissionWarpDelete"], false))
    }

    val warp = permissions.parse("WARP")!!
    val warpOthers = permissions.parse("WARP_OTHERS")!!
    val warpList = permissions.parse("WARP_LIST")!!
    val warpCreate = permissions.parse("WARP_CREATE")!!
    val warpDelete = permissions.parse("WARP_DELETE")!!
    val warpEdit = permissions.parse("WARP_EDIT")!!

    val permissionList = listOf(
        warp,
        warpOthers,
        warpList,
        warpCreate,
        warpDelete,
        warpEdit
    )
}
