package io.github.pak3nuh.monolith.service.api

import io.github.pak3nuh.monolith.core.service.Service

interface ApiClientService : Service {
    fun builder(): ApiClientBuilder
}

class ApiClientBuilder {
    fun build(): ApiClient {
        TODO()
    }
}

interface ApiClient

class ApiClientImpl: ApiClientService {
    override fun builder(): ApiClientBuilder = ApiClientBuilder()

    override fun close() {
        TODO("Not yet implemented")
    }
}
