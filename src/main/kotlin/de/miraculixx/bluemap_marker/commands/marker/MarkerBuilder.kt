package de.miraculixx.bluemap_marker.commands.marker

import com.flowpowered.math.vector.Vector2i
import com.flowpowered.math.vector.Vector3d
import de.bluecolored.bluemap.api.markers.Marker
import de.bluecolored.bluemap.api.markers.POIMarker


class MarkerBuilder(type: MarkerType) {
    val pType = type
    var pPosition: Vector3d = Vector3d.ZERO

    var pLabel: String? = null
    var pIcon: String? = null
    var pAnchor: Vector2i = Vector2i.ZERO
    var pMaxDistance: Double? = null
    var pMinDistance: Double? = null

    fun buildMarker(): Marker {
        return when (pType) {
            MarkerType.POI -> POIMarker(pLabel, pPosition).apply {
                if (pIcon != null) setIcon(pIcon, pAnchor)
                if (pMaxDistance != null) maxDistance = pMaxDistance as Double
                if (pMinDistance != null) minDistance = pMinDistance as Double
            }

            MarkerType.HTML -> TODO()
            MarkerType.LINE -> TODO()
            MarkerType.SHAPE -> TODO()
            MarkerType.EXTRUDE -> TODO()
        }
    }
}