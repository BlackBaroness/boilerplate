package io.github.blackbaroness.boilerplate

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@Suppress("SpellCheckingInspection")
fun <T : Any> wrappedLateinit(): ReadWriteProperty<Any?, T> = WrappedLateinit()

@Suppress("SpellCheckingInspection")
private class WrappedLateinit<T : Any> : ReadWriteProperty<Any?, T> {

    private lateinit var value: T

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return this.value
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }

    override fun toString() =
        "WrappedLateinit(${if (::value.isInitialized) "value=$value" else "value not initialized yet"})"
}
