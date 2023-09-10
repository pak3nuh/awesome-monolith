package io.github.pak3nuh.monolith.service.config

import io.github.pak3nuh.monolith.core.service.ServiceFactory
import io.github.pak3nuh.monolith.core.service.ServiceLocality
import io.github.pak3nuh.monolith.core.service.DependencyDeclaration
import io.github.pak3nuh.monolith.core.service.Service
import io.github.pak3nuh.monolith.core.service.ServiceDependencies
import kotlin.reflect.KClass


class ConfigurationServiceFactory: ServiceFactory<ConfigurationService> {

    override val serviceType: KClass<ConfigurationService> = ConfigurationService::class

    override val locality: ServiceLocality = ServiceLocality.LOCAL

    override val dependencies: Sequence<DependencyDeclaration<out Service>> = sequenceOf()

    override fun create(dependencies: ServiceDependencies): ConfigurationService { 
        return ConfigurationServiceImpl()
    }

}