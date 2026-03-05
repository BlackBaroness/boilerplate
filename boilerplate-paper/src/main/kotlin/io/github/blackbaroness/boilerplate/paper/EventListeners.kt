package io.github.blackbaroness.boilerplate.paper

import com.github.shynixn.mccoroutine.folia.*
import io.github.blackbaroness.boilerplate.Boilerplate
import kotlinx.coroutines.CoroutineStart
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockEvent
import org.bukkit.event.entity.EntityEvent
import org.bukkit.event.inventory.InventoryEvent
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.vehicle.VehicleEvent
import org.bukkit.event.weather.WeatherEvent
import org.bukkit.event.world.ChunkEvent
import org.bukkit.event.world.WorldEvent
import org.bukkit.plugin.Plugin
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.coroutines.CoroutineContext

private val customEventDispatcherResolvers = CopyOnWriteArrayList<(Event) -> CoroutineContext?>()

fun Boilerplate.registerEventDispatcherResolver(resolver: (Event) -> CoroutineContext?) {
    customEventDispatcherResolvers += resolver
}

fun Boilerplate.getCustomEventDispatcherResolvers(): Collection<(Event) -> CoroutineContext?> {
    return customEventDispatcherResolvers
}

/**
 * Any suspending function call will make the event pass.
 * So, if you need to modify the event result, you must use runBlocking or avoid calling suspending functions.
 */
inline fun <reified T : Event> Plugin.eventListener(
    priority: EventPriority = EventPriority.NORMAL,
    crossinline dispatcher: (T) -> CoroutineContext = { findDispatcherForEvent(this, it) },
    crossinline block: suspend (T) -> Unit,
): Listener = createEventListener<T>(priority = priority, plugin = this) { event ->
    launch(dispatcher.invoke(event), CoroutineStart.UNDISPATCHED) {
        block.invoke(event)
    }
}

inline fun <reified T : Event> createEventListener(
    plugin: Plugin,
    priority: EventPriority,
    crossinline action: (T) -> Unit,
): Listener {
    val listener = object : Listener {}
    plugin.server.pluginManager.registerEvent(
        T::class.java,
        listener,
        priority,
        { _, event -> if (event is T) action.invoke(event) },
        plugin
    )
    return listener
}

fun <T : Event> findDispatcherForEvent(plugin: Plugin, event: T): CoroutineContext {
    if (!plugin.mcCoroutineConfiguration.isFoliaLoaded) {
        // A path for non-folia is much easier.
        // "plugin.globalRegionDispatcher" is the main thread on non-folia.
        return if (event.isAsynchronous) plugin.asyncDispatcher else plugin.globalRegionDispatcher
    }

    // There are no async events in folia.
    if (event.isAsynchronous) {
        return plugin.globalRegionDispatcher
    }

    // Since each event can be executed on its specific thread, we have no choice other than trying to find it.
    for (resolver in Boilerplate.getCustomEventDispatcherResolvers()) {
        val context = resolver.invoke(event)
        if (context != null) return context
    }

    return when (event) {
        is EntityEvent -> plugin.entityDispatcher(event.entity)
        is VehicleEvent -> plugin.entityDispatcher(event.vehicle)
        is PlayerEvent -> plugin.entityDispatcher(event.player)
        is BlockEvent -> plugin.regionDispatcher(event.block.location)
        is ChunkEvent -> plugin.regionDispatcher(event.world, event.chunk.x, event.chunk.z)
        is InventoryEvent -> plugin.entityDispatcher(event.view.player)
        is WeatherEvent -> plugin.globalRegionDispatcher
        is WorldEvent -> plugin.globalRegionDispatcher
        is MCCoroutineExceptionEvent -> plugin.asyncDispatcher // can be called on different threads, IDK what to do
        else -> throw IllegalStateException("Cannot find dispatcher for ${event::class.simpleName}, override it manually")
    }
}

fun Listener.unregister() =
    HandlerList.unregisterAll(this)
