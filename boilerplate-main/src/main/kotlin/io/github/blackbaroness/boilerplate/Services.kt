package io.github.blackbaroness.boilerplate

interface Service {
    suspend fun setup() {}
    suspend fun reload() {}
    suspend fun destroy() {}
}

@Suppress("ConvertArgumentToSet", "UnusedReceiverParameter")
suspend fun <T : Service> Boilerplate.updateServiceSet(
    old: Collection<T>,
    all: Collection<T>,
    isEnabled: (T) -> Boolean,
): List<T> {
    val new = all.filter(isEnabled)

    // destroy removed
    for (service in old subtract new) {
        service.destroy()
    }

    // setup added
    for (service in new subtract old) {
        service.setup()
    }

    // reload unchanged
    for (service in new intersect old) {
        service.reload()
    }

    return new
}

