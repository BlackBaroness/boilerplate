package io.github.blackbaroness.boilerplate

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@Suppress("SpellCheckingInspection")
fun <T> nullableLateinit(): ReadWriteProperty<Any?, T> = NullableLateinit()

@Suppress("SpellCheckingInspection")
private class NullableLateinit<T> : ReadWriteProperty<Any?, T> {

    private var initialized = false
    private var value: Any? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (!this.initialized)
            error("Property ${property.name} should be initialized before get.")

        @Suppress("UNCHECKED_CAST")
        return this.value as T
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
        this.initialized = true
    }

    override fun toString() = "NullableLateInit(${if (initialized) "value=$value" else "value not initialized yet"})"
}
