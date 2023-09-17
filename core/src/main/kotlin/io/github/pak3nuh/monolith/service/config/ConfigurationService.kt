package io.github.pak3nuh.monolith.service.config

import io.github.pak3nuh.monolith.core.service.Service
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.Properties

interface ConfigurationService: Node, Service

internal class NodeBasedConfigService(private val node: Node): ConfigurationService, Node by node {

    override fun close() { }

    companion object {
        fun create(path: Path?): NodeBasedConfigService {
            val envProps = envProps("MONOLITH_")
            val jvmProps = jvmProps("monolith.")
            val fileProps = fileProps(path, "monolith.")
            val final = HashMap<String, String>(envProps.size + fileProps.size + jvmProps.size)
            final.putAll(fileProps)
            final.putAll(jvmProps)
            final.putAll(envProps)
            return NodeBasedConfigService(PropsNodeImpl(final, "."))
        }

        fun fileProps(path: Path?, prefix: String): Map<String, String> {
            if (path == null || !Files.exists(path)) {
                return emptyMap()
            }
            val properties = Properties()
            properties.load(FileInputStream(path.toFile()))
            return properties.mapKeys { it.key.toString() }
                .filter { it.key.startsWith(prefix) }
                .mapKeys { it.key.substring(prefix.length) }
                .mapValues { it.value.toString() }
        }

        fun envProps(prefix: String): Map<String, String> {
            return System.getenv()
                .filter { it.key.startsWith(prefix) }
                .mapKeys { it.key.substring(prefix.length).lowercase().replace('_', '.') }
        }

        fun jvmProps(prefix: String): Map<String, String> {
            return System.getProperties()
                .mapKeys { it.key.toString() }
                .filter { it.key.startsWith(prefix) }
                .mapKeys { it.key.substring(prefix.length) }
                .mapValues { it.value.toString() }
        }
    }

}


internal class PropsNodeImpl private constructor(
    private val properties: Map<String, String>,
    private val path: String?,
    private val separator: String
) : Node {

    constructor(properties: Map<String, String>, separator: String): this(properties, null, separator)

    override fun node(name: String): Node {
        val propName = fullPath(name)
        return PropsNodeImpl(properties, propName, separator)
    }

    override fun map(name: String): Map<String, Node> {
        return properties.keys.asSequence()
            .filter { it.startsWith(fullPath(name) + separator) }
            .map { it.removePrefix(fullPath(name) + separator) }
            .associate {
                val tokens = tokenize(it)
                val key = tokens.first()
                val node = node(name, key)
                Pair(key, node)
            }
    }

    private fun tokenize(key: String): Sequence<String> {
        return key.split(separator).asSequence()
    }

    override fun value(name: String): String? = properties[(fullPath(name))]

    private fun fullPath(name: String): String {
        return path?.plus(separator + name) ?: name
    }
}

interface Node {
    /**
     * Traverse downwards in the configuration
     */
    fun node(name: String): Node

    /**
     * Traverse multiple nodes.
     */
    fun node(vararg path: String): Node {
        return path.fold(this) { acc, name ->
            acc.node(name)
        }
    }

    /**
     * Traverse into node [name] and indexes its descendants into a map.
     * The map key is the first child after [name].
     * The map value is a node that represents the key.
     */
    fun map(name: String): Map<String, Node>

    /**
     * Extracts the value of the node [name].
     */
    fun value(name: String): String?

    /**
     * Same as [value] but with a default.
     */
    fun value(name: String, default: String): String {
        return value(name) ?: default
    }
}
