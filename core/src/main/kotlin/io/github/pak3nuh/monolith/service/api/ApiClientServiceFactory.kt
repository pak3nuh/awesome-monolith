package io.github.pak3nuh.monolith.service.api

import io.github.pak3nuh.monolith.core.service.*
import io.github.pak3nuh.monolith.service.config.ConfigurationService
import kotlin.reflect.KClass

class ApiClientServiceFactory: ServiceFactory<ApiService> {
    override val serviceType: KClass<ApiService> = ApiService::class
    override val locality: ServiceLocality = ServiceLocality.LOCAL
    private val configDep = DependencyDeclaration(ConfigurationService::class, "config")
    override val dependencies: Sequence<DependencyDeclaration<out Service>>
        get() = sequenceOf(configDep)

    override fun create(dependencies: ServiceDependencies): ApiService {
        return ApiServiceImpl()
    }

}