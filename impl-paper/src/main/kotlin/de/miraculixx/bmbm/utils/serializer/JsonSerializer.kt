package de.miraculixx.bmbm.utils.serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual

@OptIn(ExperimentalSerializationApi::class)
val json: Json = Json {
    serializersModule = SerializersModule {
        contextual(UUIDSerializer)
        contextual(Vector3dSerializer)
    }
    prettyPrint = true
    prettyPrintIndent = "  "
}