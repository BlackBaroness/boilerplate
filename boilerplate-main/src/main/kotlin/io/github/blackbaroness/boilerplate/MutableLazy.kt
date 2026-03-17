package io.github.blackbaroness.boilerplate

fun <T> mutableLazy(initializer: () -> T): MutableLazy<T> = MutableLazyImpl(initializer)

interface MutableLazy<out T> : Lazy<T> {
    fun reset()
}

private class MutableLazyImpl<T>(private val initializer: () -> T) : MutableLazy<T> {

    @Volatile
    private var holder = createHolder()

    override val value: T
        get() = holder.value

    override fun isInitialized(): Boolean {
        return holder.isInitialized()
    }

    override fun reset() {
        holder = createHolder()
    }

    private fun createHolder() = lazy(initializer)
}
