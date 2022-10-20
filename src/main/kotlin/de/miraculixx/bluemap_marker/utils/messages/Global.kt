package de.miraculixx.bluemap_marker.utils.messages

import de.miraculixx.bluemap_marker.utils.config.ConfigManager
import de.miraculixx.bluemap_marker.utils.config.Configs
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

val prefix = cmp("BannerMarker", cHighlight) + cmp(" >> ", NamedTextColor.DARK_GRAY)

fun msg(key: String, input: List<String> = emptyList(), withPrefix: Boolean = true): Component {
    return miniMessages.deserialize(
        buildString {
            val config = ConfigManager.getConfig(Configs.LANGUAGE)
            if (withPrefix) append(prefix)
            append(config.getString(key))
            if (isBlank()) append("<red>$key</red>")
            input.forEachIndexed { index, input -> replace(Regex(".*<input-$index>.*"), input) }
        }
    )
}