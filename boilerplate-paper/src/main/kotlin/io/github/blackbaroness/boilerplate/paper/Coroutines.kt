package io.github.blackbaroness.boilerplate.paper

import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mccoroutine.folia.scope
import kotlinx.coroutines.*
import org.bukkit.plugin.Plugin
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
