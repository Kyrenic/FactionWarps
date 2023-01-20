package me.kyrenic.factionwarps.language

import me.kyrenic.factionwarps.FactionWarps
import java.io.File
import java.net.URLClassLoader
import java.text.MessageFormat
import java.util.*

class Language(plugin: FactionWarps, private val language: String) {

    private val resourceBundles: List<ResourceBundle>
    val locale = Locale.forLanguageTag(language)

    init {
        // Get language folder, create if it's missing.
        val languageFolder = File(plugin.dataFolder, "language")
        if (!languageFolder.exists()) {
            languageFolder.mkdirs()
        }

        // Get en_US file, create if it's missing.
        val enUsFilename = "language/language_en_US.properties"
        val enUsFile = File(enUsFilename)
        if (!enUsFile.exists()) {
            plugin.saveResource(enUsFilename, false)
        }

        // Get de_DE file, create if it's missing.
        val deDeFilename = "language/language_de_DE.properties"
        val deDeFile = File(deDeFilename)
        if (!deDeFile.exists()) {
            plugin.saveResource(deDeFilename, false)
        }

        val externalUrls = arrayOf(languageFolder.toURI().toURL())
        val externalClassLoader = URLClassLoader(externalUrls)
        val externalResourceBundle = ResourceBundle.getBundle("language", locale, externalClassLoader)
        val internalResourceBundle = ResourceBundle.getBundle("language/language", locale)
        resourceBundles = listOf(externalResourceBundle, internalResourceBundle)
    }

    operator fun get(key: String, vararg params: String) =
        resourceBundles.firstNotNullOfOrNull { resourceBundle ->
            try {
                MessageFormat.format(resourceBundle.getString(key), *params)
            } catch (exception: MissingResourceException) {
                null
            }
        } ?: "Missing language entry for $language: $key"
}
