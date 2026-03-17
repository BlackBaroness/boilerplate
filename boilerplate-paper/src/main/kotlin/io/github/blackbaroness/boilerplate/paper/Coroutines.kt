package io.github.blackbaroness.boilerplate.paper

import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mccoroutine.folia.scope
import kotlinx.coroutines.*
import org.bukkit.plugin.Plugin
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration

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

fun Plugin.ticker(
    period: Duration,
    context: CoroutineContext = Dispatchers.Default,
    action: suspend () -> Unit,
): Job = ticker({ period }, context, action)

fun Plugin.ticker(
    period: () -> Duration,
    context: CoroutineContext = Dispatchers.Default,
    action: suspend () -> Unit,
) = launch(context) {
    while (isActive) {
        try {
            action.invoke()
        } catch (e: Throwable) {
            slF4JLogger.error("Error in ticker", e)
        }

        delay(period.invoke())
    }
}
