package io.github.pak3nuh.monolith.storage

interface StorageClient<K, V> {
    fun get(key: K): V?
    fun set(key: K, value: V)
}

