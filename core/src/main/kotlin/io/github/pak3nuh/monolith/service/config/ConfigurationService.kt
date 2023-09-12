package io.github.pak3nuh.monolith.service.config

import io.github.pak3nuh.monolith.core.service.Service
import java.io.FileInputStream
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Properties

interface ConfigurationService: Node, Service

internal class NodeBasedConfigService(private val node: Node): ConfigurationService, Node by node {

    override fun close() { }

    companion object {
        fun create(path: Path): NodeBasedConfigService {
            val envProps = envProps("MONOLITH_")
            val fileProps = fileProps(path)
            val final = HashMap<String, String>(envProps.size + fileProps.size)
            final.putAll(fileProps)
            final.putAll(envProps)
            return NodeBasedConfigService(PropsNodeImpl(final, "", "."))
        }

        fun fileProps(path: Path): Map<String, String> {
            val properties = Properties()
            properties.load(FileInputStream(path.toFile()))
            return properties.mapKeys { it.key.toString() }.mapValues { it.value.toString() }
        }

        fun envProps(prefix: String): Map<String, String> {
            return System.getenv()
                .filter { it.key.startsWith(prefix) }
                .mapKeys { it.key.substring(prefix.length).lowercase().replace('_', '.') }
        }
    }

}


internal class PropsNodeImpl(
    private val properties: Map<String, String>,
    private val path: String,
    private val separator: String
) : Node {

    override fun node(name: String): Node {
        val propName = compose(name)
        return PropsNodeImpl(properties, propName, separator)
    }

    override fun list(name: String): Collection<Node> {
        return map(name).values
    }

    override fun map(name: String): Map<String, Node> {
        return properties.keys
            .filter { it.startsWith(compose(name) + separator) }
            .associate {
                TODO("need to get the actual map key, name is just the collection name")
                Pair(name, node(name))
            }
    }

    override fun value(name: String): String? = properties[(compose(name))]

    private fun compose(name: String): String = path + separator + name
}

interface Node {
    fun node(name: String): Node
    fun node(vararg path: String): Node {
        return path.fold(this) { acc, name ->
            acc.node(name)
        }
    }
    fun list(name: String): Collection<Node>
    fun map(name: String): Map<String, Node>
    fun value(name: String): String?
}
