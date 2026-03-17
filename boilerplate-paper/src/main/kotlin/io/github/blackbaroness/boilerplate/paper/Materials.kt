package io.github.blackbaroness.boilerplate.paper

import io.github.blackbaroness.boilerplate.Boilerplate
import org.bukkit.Material

@Suppress("UnusedReceiverParameter")
val Boilerplate.allValidMaterials: Sequence<Material>
    get() = Material.entries.asSequence()
        .filter { !it.isAir }
        .filter { it.isItem }
        .filter { !it.name.startsWith("LEGACY_") }
