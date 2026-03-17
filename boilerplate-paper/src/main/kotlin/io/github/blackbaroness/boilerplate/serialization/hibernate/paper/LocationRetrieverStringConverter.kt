package io.github.blackbaroness.boilerplate.serialization.hibernate.paper

import io.github.blackbaroness.boilerplate.paper.LocationRetriever
import jakarta.persistence.AttributeConverter
import kotlinx.serialization.json.Json

class LocationRetrieverStringConverter : AttributeConverter<LocationRetriever, String> {

    override fun convertToDatabaseColumn(value: LocationRetriever?): String? =
        value?.let { Json.encodeToString(it) }

    override fun convertToEntityAttribute(value: String?): LocationRetriever? =
        value?.let { Json.decodeFromString<LocationRetriever>(it) }
}
