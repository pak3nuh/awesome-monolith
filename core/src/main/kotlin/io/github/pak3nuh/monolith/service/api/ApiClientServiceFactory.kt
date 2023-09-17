package io.github.pak3nuh.monolith.service.api

import io.github.pak3nuh.monolith.core.service.*
import io.github.pak3nuh.monolith.service.config.ConfigurationService
import kotlin.reflect.KClass

class ApiClientServiceFactory: ServiceFactory<ApiClientService> {
    override val serviceType: KClass<ApiClientService> = ApiClientService::class
    override val locality: ServiceLocality = ServiceLocality.LOCAL
    private val configDep = DependencyDeclaration(ConfigurationService::class, "config")
    override val dependencies: Sequence<DependencyDeclaration<out Service>>
        get() = sequenceOf(configDep)

    override fun create(dependencies: ServiceDependencies): ApiClientService {
        return ApiClientImpl()
    }

}