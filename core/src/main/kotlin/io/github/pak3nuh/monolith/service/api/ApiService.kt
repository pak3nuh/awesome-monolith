package io.github.pak3nuh.monolith.service.api

import io.github.pak3nuh.monolith.core.service.Service
import io.github.pak3nuh.monolith.service.api.rest.ResourceRegistrar

interface ApiService : Service {
    fun clientBuilder(): ApiClientBuilder
    fun restResource(): ResourceRegistrar
}

class ApiClientBuilder {
    fun build(): ApiClient {
        TODO("Not yet implemented")
    }
}

interface ApiClient

class ApiServiceImpl: ApiService {
    override fun clientBuilder(): ApiClientBuilder = ApiClientBuilder()

    override fun restResource(): ResourceRegistrar {
        TODO("Not yet implemented")
    }

    override fun close() {
        TODO("Not yet implemented")
    }
}
