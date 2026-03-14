package io.github.blackbaroness.boilerplate.bungeecord

import com.github.shynixn.mccoroutine.bungeecord.launch
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import net.bytebuddy.ByteBuddy
import net.bytebuddy.description.modifier.FieldManifestation
import net.bytebuddy.description.modifier.Visibility
import net.bytebuddy.implementation.FieldAccessor
import net.bytebuddy.implementation.MethodCall
import net.md_5.bungee.api.event.AsyncEvent
import net.md_5.bungee.api.plugin.Event
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.event.EventHandler
import net.md_5.bungee.event.EventPriority
import org.jetbrains.annotations.ApiStatus
import java.io.Closeable
import java.lang.reflect.Modifier
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass

inline fun <reified EVENT : Event> Plugin.eventListener(
    priority: Byte = EventPriority.NORMAL,
    noinline action: (EVENT) -> Unit,
) = provideEventListener(this, EVENT::class, priority = priority, action = action)

inline fun <reified EVENT : Event> Plugin.eventListenerAsync(
    priority: Byte = EventPriority.NORMAL,
    context: CoroutineContext = Dispatchers.Default,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    crossinline action: suspend (EVENT) -> Unit,
) = provideEventListener(this, EVENT::class, priority = priority) { event ->
    launch(context, start) {
        action.invoke(event)
    }
}

inline fun <reified EVENT : AsyncEvent<*>> Plugin.eventListenerIntent(
    priority: Byte = EventPriority.NORMAL,
    context: CoroutineContext = Dispatchers.Default,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    crossinline action: suspend (EVENT) -> Unit,
) = provideEventListener(this, EVENT::class, priority = priority) { event ->
    event.registerIntent(this)
    try {
        launch(context, start) {
            try {
                action.invoke(event)
            } finally {
                event.completeIntent(this@eventListenerIntent)
            }
        }
    } catch (t: Throwable) {
        event.completeIntent(this@eventListenerIntent)
        throw t
    }
}

private data class EventClassKey(val clazz: Class<*>, val priority: Byte)

private val eventListenerCache = ConcurrentHashMap<EventClassKey, Class<*>>()

@ApiStatus.Internal
fun <T : Event> provideEventListener(
    plugin: Plugin,
    eventClass: KClass<T>,
    priority: Byte = EventPriority.NORMAL,
    action: (T) -> Unit,
): Closeable {
    val listenerClass = eventListenerCache
        .computeIfAbsent(EventClassKey(eventClass.java, priority)) { key -> generateEventListenerClass(plugin, key) }

    val listener = listenerClass
        .getConstructor(Consumer::class.java)
        .newInstance(Consumer<T> { action.invoke(it) })
        as Listener

    plugin.proxy.pluginManager.registerListener(plugin, listener)
    return Closeable { plugin.proxy.pluginManager.unregisterListener(listener) }
}

private fun generateEventListenerClass(plugin: Plugin, key: EventClassKey): Class<*> {
    return ByteBuddy().subclass(Listener::class.java).run {
        modifiers(Modifier.PUBLIC)
        name("${plugin::class.java.packageName}.__generated__.Listener_${key.clazz.name}_${key.priority}")

        defineField("action", Consumer::class.java, Visibility.PRIVATE, FieldManifestation.FINAL)

        defineConstructor(Visibility.PUBLIC)
            .withParameter(Consumer::class.java)
            .intercept(
                MethodCall.invoke(Any::class.java.getConstructor())
                    .onSuper()
                    .andThen(FieldAccessor.ofField("action").setsArgumentAt(0))
            )

        defineMethod("handleEvent", Void.TYPE, Modifier.PUBLIC)
            .withParameters(key.clazz)
            .intercept(
                MethodCall
                    .invoke(Consumer::class.java.getMethod("accept", Any::class.java))
                    .onField("action")
                    .withArgument(0)
            )
            .annotateMethod(EventHandler(priority = key.priority))

        make()
            .load(plugin::class.java.classLoader)
            .loaded
    }
}
