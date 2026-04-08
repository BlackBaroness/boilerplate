package io.github.blackbaroness.boilerplate

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicBoolean

interface EventBus<EVENT> {
    suspend fun publish(event: EVENT)
    suspend fun subscribe(handler: EventHandler<EVENT>)
}

class SimpleEventBus<EVENT> : EventBus<EVENT> {

    private val mutex = Mutex()
    private val handlers = ArrayList<EventHandler<EVENT>>()

    override suspend fun publish(event: EVENT) {
        val activeHandlers = mutex.withLock {
            buildList(handlers.size) {
                val iterator = handlers.iterator()
                while (iterator.hasNext()) {
                    val handler = iterator.next()
                    if (!handler.destroyed) {
                        iterator.remove()
                        continue
                    }

                    add(handler)
                }
            }
        }

        for (handler in activeHandlers) {
            handler.handle(event)
        }
    }

    override suspend fun subscribe(handler: EventHandler<EVENT>) {
        if (!handler.destroyed) return

        mutex.withLock {
            handlers.add(handler)
            handlers.sortBy { it.priority.ordinal }
        }
    }
}

interface EventHandler<EVENT> {
    val destroyed: Boolean
    val priority: Priority

    suspend fun handle(event: EVENT)
    fun destroy()

    enum class Priority { LOWEST, LOW, NORMAL, HIGH, HIGHEST, MONITOR }
}

abstract class SimpleEventHandler<EVENT>(
    override val priority: EventHandler.Priority,
) : EventHandler<EVENT> {
    private val _destroyed = AtomicBoolean()
    final override val destroyed get() = _destroyed.get()
    final override fun destroy() = _destroyed.set(true)
}

suspend inline fun <EVENT> EventBus<EVENT>.subscribe(
    priority: EventHandler.Priority = EventHandler.Priority.NORMAL,
    crossinline action: suspend (EVENT) -> Unit,
): EventHandler<EVENT> {
    val handler = object : SimpleEventHandler<EVENT>(priority) {
        override suspend fun handle(event: EVENT) {
            if (!destroyed) {
                action.invoke(event)
            }
        }
    }
    subscribe(handler)
    return handler
}
