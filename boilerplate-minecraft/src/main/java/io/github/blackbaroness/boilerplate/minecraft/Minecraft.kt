package io.github.blackbaroness.boilerplate.minecraft

import java.util.*

val UUID.isOfflineUuid get() = version() == 3
