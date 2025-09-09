package de.miraculixx.bmbm.utils

import de.miraculixx.bmbm.utils.messages.cError
import de.miraculixx.bmbm.utils.messages.cSuccess
import de.miraculixx.bmbm.utils.messages.prefix
import de.miraculixx.bmbm.utils.serializer.json
import de.miraculixx.kpaper.extensions.bukkit.cmp
import de.miraculixx.kpaper.extensions.bukkit.plus
import de.miraculixx.kpaper.extensions.console
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component
import java.net.HttpURLConnection
import java.net.URI

object APIConnector {
    var isOutdated = false
    var outdatedMessage: Component = Component.empty().asComponent()

    fun checkVersion(currentVersion: Int): Boolean {
        val version = try {
            val url = URI.create("https://api.mutils.de/public/version").toURL()
            val con = url.openConnection() as HttpURLConnection
            con.requestMethod = "GET"
            con.setRequestProperty("User-Agent", "MUtils-API-1.1")
            con.setRequestProperty("Service", "BMBM")
            con.doInput = true
            con.doOutput = true
            con.connect()
            json.decodeFromString<Version>(con.inputStream.readBytes().decodeToString())
        } catch (e: Exception) {
            null
        }
        if (version == null) {
            console.sendMessage(prefix + cmp("Could not check current version! Proceed at your own risk", cError))
            return true
        }
        outdatedMessage = cmp("Latest Version: ") + cmp(version.latest.toString(), cSuccess) + cmp(" - Installed Version: ") + cmp(currentVersion.toString(), cError)
        if (currentVersion < version.last) {
            console.sendMessage(prefix + cmp("You are running a too outdated version of BMM! An update is required due to security reasons or internal changes.", cError))
            console.sendMessage(prefix + outdatedMessage)
            isOutdated = true
            return false
        }
        if (currentVersion < version.latest) {
            console.sendMessage(prefix + cmp("You are running an outdated version of BMM!"))
            console.sendMessage(prefix + outdatedMessage)
            isOutdated = true
        }
        if (currentVersion > version.latest) {
            console.sendMessage(prefix + cmp("You are running a beta version. Bugs may appear!"))
        }
        return true
    }

    @Serializable
    private data class Version(val latest: Int, val last: Int)
}