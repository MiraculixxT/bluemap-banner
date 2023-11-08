package de.miraculixx.bmbm.utils.serializer

import de.miraculixx.kpaper.serialization.UUIDSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual

val json: Json = Json {
    serializersModule = SerializersModule {
        contextual(UUIDSerializer)
        contextual(Vector3dSerializer)
    }
    prettyPrint = true
}