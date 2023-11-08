package de.miraculixx.bmbm.utils.serializer

import com.flowpowered.math.vector.Vector3d
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object Vector3dSerializer : KSerializer<Vector3d> {
    override val descriptor = PrimitiveSerialDescriptor("Vector3D", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Vector3d {
        val split = decoder.decodeString().split(':')
        return Vector3d.from(split[0].toDouble(), split[1].toDouble(), split[2].toDouble())
    }

    override fun serialize(encoder: Encoder, value: Vector3d) {
        encoder.encodeString("${value.x}:${value.y}:${value.z}")
    }
}