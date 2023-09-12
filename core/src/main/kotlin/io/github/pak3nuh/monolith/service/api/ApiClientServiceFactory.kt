package io.github.pak3nuh.monolith.service.api

import io.github.pak3nuh.monolith.core.service.*
import io.github.pak3nuh.monolith.service.config.ConfigurationService
import kotlin.reflect.KClass

class ApiClientServiceFactory: ServiceFactory<ApiClientService> {
    override val serviceType: KClass<ApiClientService> = ApiClientService::class
    override val locality: ServiceLocality = ServiceLocality.LOCAL
    override val dependencies: Sequence<DependencyDeclaration<out Service>>
        get() = sequenceOf()

    override fun create(dependencies: ServiceDependencies): ApiClientService {
        return ApiClientImpl()
    }

}