package me.kyrenic.factionwarps.services

import org.bukkit.plugin.Plugin

class ConfigService(private val plugin: Plugin) {
    private val changes = mutableMapOf<String, String>()

    fun set(option: String, value: String) {
        changes[option] = value
    }

    fun reset(option: String) {
        changes[option] = plugin.config.defaults?.getString(option) ?: return
    }

    fun apply() {
        for ((option, value) in changes) {
            plugin.config.set(option, value)
        }
        plugin.saveConfig()
    }
}