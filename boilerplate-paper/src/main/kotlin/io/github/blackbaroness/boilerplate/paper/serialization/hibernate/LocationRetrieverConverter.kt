package io.github.blackbaroness.boilerplate.paper.serialization.hibernate

import io.github.blackbaroness.boilerplate.paper.model.LocationRetriever
import jakarta.persistence.AttributeConverter
import kotlinx.serialization.json.Json

class LocationRetrieverConverter : AttributeConverter<LocationRetriever, String> {

    override fun convertToDatabaseColumn(value: LocationRetriever?): String? =
        value?.let { Json.encodeToString(it) }

    override fun convertToEntityAttribute(value: String?): LocationRetriever? =
        value?.let { Json.decodeFromString<LocationRetriever>(it) }
}
