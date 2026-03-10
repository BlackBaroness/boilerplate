package io.github.blackbaroness.boilerplate

import com.charleskorn.kaml.YamlMap
import com.charleskorn.kaml.YamlNode
import com.charleskorn.kaml.yamlMap
import com.charleskorn.kaml.yamlScalar

fun YamlMap.navigate(vararg path: String): YamlNode {
    var current: YamlNode = this
    for (string in path) {
        current = current.yamlMap.get<YamlNode>(string) ?: error("Path '$string' not found on ${current.path}")
    }
    return current
}

val YamlNode.asString: String
    get() = yamlScalar.content
