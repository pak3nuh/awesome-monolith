package io.github.pak3nuh.monolith.service.api.rest

import java.io.InputStream

enum class Method {
    GET, POST, PATCH, PUT
}

data class ResourceDefinition(
    val path: String,
    val methods: Set<Method>,
    val mimeTypes: Set<String>
)

data class Request(
    val path: String,
    val parameters: Map<String, String>,
    val body: Body?
)

data class Body(
    val mimeType: String,
    val body: InputStream
)

data class Response(
    val status: UInt,
    val message: String?,
    val body: Body?
)

interface ResourceRegistrar {
    fun registerResource(resourceDefinition: ResourceDefinition, responseHandler: ResponseHandler)
}

interface ResponseHandler {
    suspend fun handle(request: Request): Response
}
