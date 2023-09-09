package io.github.pak3nuh.monolith.service.user

import io.github.pak3nuh.monolith.service.api.ApiClientService

internal class RemoteUserService(private val apiClientService: ApiClientService): UserService {
    override fun close() {
        apiClientService.close()
    }
}
