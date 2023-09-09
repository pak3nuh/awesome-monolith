package io.github.pak3nuh.monolith.service.user

import io.github.pak3nuh.monolith.core.service.DependencyDeclaration
import io.github.pak3nuh.monolith.core.service.Service
import io.github.pak3nuh.monolith.core.service.ServiceDependencies
import io.github.pak3nuh.monolith.service.api.ApiClientService
import kotlin.reflect.KClass

class RemoteUserServiceFactory: UserServiceFactory {
    override val serviceType: KClass<UserService>
        get() = UserService::class
    override val dependencies: Sequence<DependencyDeclaration<out Service>>
        get() = sequenceOf(apiClientDependency)

    private val apiClientDependency = DependencyDeclaration(ApiClientService::class, "api-client")

    override fun create(dependencies: ServiceDependencies): UserService {
        val apiClientService: ApiClientService = dependencies.getService(apiClientDependency)
        return RemoteUserService(apiClientService)
    }
}