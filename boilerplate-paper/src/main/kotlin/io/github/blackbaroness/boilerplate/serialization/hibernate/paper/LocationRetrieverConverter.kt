package io.github.blackbaroness.boilerplate.serialization.hibernate.paper

import io.github.blackbaroness.boilerplate.paper.model.LocationRetriever
import jakarta.persistence.AttributeConverter
import kotlinx.serialization.json.Json

object LocationRetrieverConverter : AttributeConverter<LocationRetriever, String> {

    override fun convertToDatabaseColumn(value: LocationRetriever?): String? =
        value?.let { Json.encodeToString(it) }

    override fun convertToEntityAttribute(value: String?): LocationRetriever? =
        value?.let { Json.decodeFromString<LocationRetriever>(it) }
}
