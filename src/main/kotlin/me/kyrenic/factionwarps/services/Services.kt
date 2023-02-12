package me.kyrenic.factionwarps.services

class Services(
    val warpService: WarpService,
    val configService: ConfigService,
    val teleportService: TeleportService,
    val logService: LogService,
    val cooldownService: CooldownService
)