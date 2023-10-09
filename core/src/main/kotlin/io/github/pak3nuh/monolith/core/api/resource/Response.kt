package io.github.pak3nuh.monolith.core.api.resource

data class Response(
    val status: UInt,
    val message: String?,
    val body: Body?
)