package io.github.pak3nuh.monolith.storage

class InMemStorageClient<K, V>: StorageClient<K, V> {
    private val table = mutableMapOf<K, V>()

    override fun get(key: K): V? = table[key]

    override fun set(key: K, value: V) {
        table[key] = value
    }
}