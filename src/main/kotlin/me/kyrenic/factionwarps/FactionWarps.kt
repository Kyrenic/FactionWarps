package me.kyrenic.factionwarps

import com.dansplugins.factionsystem.MedievalFactions
import com.dansplugins.factionsystem.faction.role.MfFactionRole
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import me.kyrenic.factionwarps.commands.faction.warp.FactionWarpCommands
import me.kyrenic.factionwarps.jooq.JooqWarpService
import me.kyrenic.factionwarps.language.Language
import me.kyrenic.factionwarps.permission.FactionWarpsFactionPermissions
import me.kyrenic.factionwarps.services.ConfigService
import me.kyrenic.factionwarps.services.Services
import me.kyrenic.factionwarps.services.WarpService
import org.bukkit.command.CommandMap
import org.bukkit.command.PluginCommand
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.SimplePluginManager
import org.bukkit.plugin.java.JavaPlugin
import org.flywaydb.core.Flyway
import org.jooq.SQLDialect
import org.jooq.conf.Settings
import org.jooq.impl.DSL
import java.lang.reflect.Constructor
import javax.sql.DataSource

class FactionWarps : JavaPlugin() {

    private lateinit var dataSource: DataSource

    lateinit var medievalFactions: MedievalFactions

    lateinit var factionPermissions: FactionWarpsFactionPermissions

    lateinit var services: Services
    lateinit var language: Language

    override fun onEnable() {
        saveDefaultConfig()

        // Get Medieval Factions, disable if not found.
        val medievalFactions = server.pluginManager.getPlugin("MedievalFactions") as? MedievalFactions
        if (medievalFactions == null) {
            logger.severe("Medieval Factions was not found, disabling Faction Warps...")
            isEnabled = false
            return
        } else {
            this.medievalFactions = medievalFactions
        }

        // Load the language file.
        language = Language(this, config.getString("language") ?: "en-US")

        // Database thingamabobs.
        Class.forName("org.h2.Driver")
        val databaseUrl = config.getString("database.url")
        val databaseUsername = config.getString("database.username") ?: ""
        val databasePassword = config.getString("database.password") ?: ""
        val hikariConfig = HikariConfig()
        if (databaseUrl != null) {
            hikariConfig.jdbcUrl = databaseUrl
            hikariConfig.username = databaseUsername
            hikariConfig.password = databasePassword
        } else {
            logger.severe("Database defined incorrectly, disabling Faction Warps...")
            isEnabled = false
        }
        dataSource = HikariDataSource(hikariConfig)

        val flyway = Flyway.configure(classLoader)
            .dataSource(dataSource)
            .locations("classpath:me/kyrenic/factionwarps/db")
            .table("fw_schema_history")
            .baselineOnMigrate(true)
            .baselineVersion("0")
            .validateOnMigrate(false)
            .load()
        flyway.migrate()

        System.setProperty("org.jooq.no-logo", "true")
        System.setProperty("org.jooq.no-tips", "true")

        val dialect = config.getString("database.dialect")?.let(SQLDialect::valueOf)
        val jooqSettings = Settings().withRenderSchema(false)
        val dsl = DSL.using(
            dataSource,
            dialect,
            jooqSettings
        )

        // Create services.
        val jooqWarpService = JooqWarpService(dsl, this)

        val warpService = WarpService(this, jooqWarpService)
        val configService = ConfigService(this)

        services = Services(
            warpService,
            configService
        )

        // Add permissions.
        factionPermissions = FactionWarpsFactionPermissions(this)
        val factionService = medievalFactions.services.factionService
        var factionsChanged = 0
        // Loop through all the factions.
        factionService.factions.forEach { faction ->
            factionsChanged++
            val newDefaultPermissionsByName = faction.defaultPermissionsByName.toMutableMap()
            var newRoles: List<MfFactionRole> = faction.roles.copy()
            // Loop through all the permissions that this extension adds.
            factionPermissions.permissionList.forEach { permission ->
                // Check if the permission is present in the list, if not add it.
                if (!faction.defaultPermissions.containsKey(permission)) {
                    // Add it to the default permission list:
                    newDefaultPermissionsByName[permission.name] = permission.default
                    // Add it to the high-ranking roles:
                    newRoles = newRoles.map { role ->
                        // Build a new permissions map.
                        val permissionsToAdd = buildMap {
                            if (role.hasPermission(faction, medievalFactions.factionPermissions.changeName)) {
                                put(permission.name, true)
                            }
                        }
                        // Add the map to the existing map.
                        role.copy(permissionsByName = role.permissionsByName + permissionsToAdd)
                    }
                }
            }
            factionService.save(faction.copy(defaultPermissionsByName = newDefaultPermissionsByName, roles = faction.roles.copy(roles = newRoles)))
            logger.info("Added Faction Warps faction permissions to $factionsChanged factions...")
        }

        // Register listeners.
        // To add: unclaim, overclaim, faction disband

        // Set command executors.
        getDynamicCommand(language["CommandWarp"])?.setExecutor(FactionWarpCommands(this))
    }

    private fun getCommandMap(): CommandMap {
        val commandMapField = SimplePluginManager::class.java.getDeclaredField("commandMap")
        commandMapField.isAccessible = true
        return commandMapField.get(server.pluginManager) as CommandMap
    }

    private fun getDynamicCommand(name: String, vararg aliases: String): PluginCommand {
        val constructor: Constructor<PluginCommand> = PluginCommand::class.java.getDeclaredConstructor(String::class.java, Plugin::class.java)
        constructor.isAccessible = true
        val command = constructor.newInstance(name, this)
        command.aliases = aliases.toList()
        getCommandMap().register(description.name, command)
        return command
    }

    override fun onDisable() {
        // Apply config changes.
    }
}