package io.github.blackbaroness.boilerplate.bungeecord

import com.github.shynixn.mccoroutine.bungeecord.launch
import com.github.shynixn.mccoroutine.bungeecord.scope
import kotlinx.coroutines.*
import net.md_5.bungee.api.plugin.Plugin
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun <T> Plugin.lazy(
    context: CoroutineContext = EmptyCoroutineContext,
    supplier: suspend CoroutineScope.() -> T,
) = scope.async(context = context, start = CoroutineStart.LAZY, block = supplier)

fun <T> Plugin.async(
    context: CoroutineContext = EmptyCoroutineContext,
    supplier: suspend CoroutineScope.() -> T,
) = scope.async(context = context, block = supplier)

fun Plugin.after(
    job: Job,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    action: suspend (cause: Throwable?) -> Unit,
): DisposableHandle = job.invokeOnCompletion { cause ->
    this.launch(dispatcher) {
        action.invoke(cause)
    }
}
