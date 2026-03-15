package io.github.blackbaroness.boilerplate

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

const val WRITE_ONLY_MESSAGE = "This property is write-only"

inline fun <reified T> writeOnly(crossinline writer: (T) -> Unit) = object : WriteOnlyProperty<T> {
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        writer.invoke(value)
    }
}

interface WriteOnlyProperty<T> : ReadWriteProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        throw NotImplementedError("This is a write-only property!")
    }
}
