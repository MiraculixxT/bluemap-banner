package de.miraculixx.bluemap_marker.utils.messages

import de.miraculixx.bluemap_marker.utils.config.ConfigManager
import de.miraculixx.bluemap_marker.utils.config.Configs
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

val prefix = cmp("BannerMarker", cHighlight) + cmp(" >> ", NamedTextColor.DARK_GRAY)

fun msg(key: String, input: List<String> = emptyList(), withPrefix: Boolean = true): Component {
    val config = ConfigManager.getConfig(Configs.LANGUAGE)
    var buildString = config.getString(key) ?: "<red>$key</red>"
    input.forEachIndexed { index, i -> buildString = buildString.replace("<input-${index + 1}>", i) }
    return if (withPrefix) prefix else { emptyComponent() } + miniMessages.deserialize(buildString)
}