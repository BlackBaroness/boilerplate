package io.github.blackbaroness.boilerplate.minecraft.mojang.profile

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class MojangPlayerProfile @OptIn(ExperimentalUuidApi::class) constructor(
    val id: Uuid,
    val name: String,
    val properties: List<JsonObject>,
) {

    @OptIn(ExperimentalSerializationApi::class)
    @JsonIgnoreUnknownKeys
    @Serializable
    data class Texture(
        val signature: String?,
        val value: String,
    )

    @delegate:Transient
    val texture: Texture? by lazy {
        properties.find { it["name"]!!.jsonPrimitive.content == "textures" }?.let {
            Json.decodeFromJsonElement<Texture>(it)
        }
    }
}
