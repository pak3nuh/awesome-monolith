package io.github.pak3nuh.monolith.service.api.rest

import io.github.pak3nuh.monolith.core.api.resource.Request
import io.github.pak3nuh.monolith.core.api.resource.Definition
import io.github.pak3nuh.monolith.core.api.resource.Response

interface ResourceRegistrar {
    fun registerResource(
        definition: Definition,
        interceptors: List<RequestInterceptor>,
        responseHandler: ResponseHandler
    )
}

fun interface ResponseHandler {
    suspend fun handle(request: Request): Response
}

fun interface RequestInterceptor {
    suspend fun intercept(request: Request, next: ResponseHandler): Response
}

/**
 * Merges the list of interceptors with the response handler into a single handler chaining calls together.
 */
internal fun requestChain(interceptors: List<RequestInterceptor>, handler: ResponseHandler): ResponseHandler {
    return if (interceptors.isEmpty()) {
        handler
    } else {
        val reduced = interceptors.reduce { firstInt, secondInt ->
            RequestInterceptor { req, nextHandler ->
                firstInt.intercept(req) { secondInt.intercept(it, nextHandler) }
            }
        }
        ResponseHandler { request -> reduced.intercept(request, handler) }
    }
}
