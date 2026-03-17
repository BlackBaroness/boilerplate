package io.github.blackbaroness.boilerplate.serialization.hibernate

import io.github.blackbaroness.boilerplate.toInetAddress
import io.github.blackbaroness.boilerplate.toInt
import jakarta.persistence.AttributeConverter
import java.net.InetAddress

class InetAddressIntConverter : AttributeConverter<InetAddress, Int> {

    override fun convertToDatabaseColumn(attribute: InetAddress?): Int? {
        return attribute?.toInt()
    }

    override fun convertToEntityAttribute(dbData: Int?): InetAddress? {
        return dbData?.toInetAddress()
    }
}
