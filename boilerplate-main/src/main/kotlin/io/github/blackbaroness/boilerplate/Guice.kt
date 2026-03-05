package io.github.blackbaroness.boilerplate

import com.google.inject.Module
import com.google.inject.assistedinject.FactoryModuleBuilder

inline fun <reified T> Boilerplate.createAssistedFactory(): Module =
    FactoryModuleBuilder().build(T::class.java)
