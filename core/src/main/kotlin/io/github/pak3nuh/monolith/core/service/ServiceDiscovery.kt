package io.github.pak3nuh.monolith.core.service

import io.github.pak3nuh.monolith.service.config.ConfigurationService
import io.github.pak3nuh.monolith.service.config.NodeBasedConfigService
import java.nio.file.Paths
import java.util.*
import kotlin.reflect.KClass

object ServiceDiscovery {

    fun boostrapServices(): LoadedServices {
        val factoryLoader: ServiceLoader<ServiceFactory<*>> = ServiceLoader.load(ServiceFactory::class.java)
        val factories: Sequence<ServiceFactory<*>> = factoryLoader.asSequence()
        val configService = createConfigService()
        val config = mergeConfiguration(
            getDefaultConfiguration(factories),
            getUserConfiguration(configService)
        )
        val filtered = filter(config, factories)
        val services = loadServices(filtered, configService)
        return LoadedServices(services)
    }

    internal fun createConfigService(): ConfigurationService {
        val cfgPath = System.getenv("MONOLITH_CONFIG_FILE_PATH") ?: "config.properties"
        return NodeBasedConfigService.create(Paths.get(cfgPath))
    }

    internal fun getDefaultConfiguration(factories: Sequence<ServiceFactory<*>>): Sequence<ServiceBootstrapConfiguration> {
        return factories.map { ServiceBootstrapConfiguration(it::class, ServiceLocality.LOCAL, true) }
    }

    internal fun getUserConfiguration(configService: ConfigurationService): Sequence<ServiceBootstrapConfiguration> {
        return configService.node("bootstrap")
            .list("services")
            .map {
                ServiceBootstrapConfiguration(
                    Class.forName(requireNotNull(it.value("factory"))).kotlin as KClass<out ServiceFactory<*>>,
                    ServiceLocality.valueOf(it.value("locality") ?: "LOCAL"),
                    it.value("enabled")?.toBoolean() ?: true
                )
            }.asSequence()
    }

    internal fun mergeConfiguration(
        config: Sequence<ServiceBootstrapConfiguration>, 
        overrides: Sequence<ServiceBootstrapConfiguration>
    ): Sequence<ServiceBootstrapConfiguration> {
        val finalConfig = mutableListOf<ServiceBootstrapConfiguration>()
        finalConfig.addAll(config.filter { !overrides.contains(it) })
        finalConfig.addAll(overrides)
        return finalConfig.asSequence()
    }

    internal fun filter(config: Sequence<ServiceBootstrapConfiguration>, factories: Sequence<ServiceFactory<*>>): Sequence<ServiceFactory<*>> {
        val serviceMap = config.associateBy { it.type }
        return factories
            .map {
                val factoryType = it::class
                val serviceConfig = requireNotNull(serviceMap[factoryType]) { "There is no configuration for factory $factoryType" }
                Pair(serviceConfig, it)
            }
            .filter { it.first.enabled }
            .filter { it.first.locality == it.second.locality }
            .map { it.second }
    }

    internal fun loadServices(factories: Sequence<ServiceFactory<*>>, configService: ConfigurationService): Map<KClass<out Service>, ServiceCreation> {
        val loadedServices = mutableMapOf<KClass<out Service>, ServiceCreation>()
        loadedServices[configService::class] = SingletonService(configService)
        factories.sortedBy { it.dependencies.count() }
            .forEach { factory ->
                val serviceType = factory.serviceType
                require(!loadedServices.containsKey(serviceType)) { "Service $serviceType already loaded" }
                val dependencies = getDependencies(loadedServices, factory.dependencies)
                val serviceInstance = factory.create(dependencies)
                loadedServices[serviceType] = SingletonService(serviceInstance)
            }
        return loadedServices
    }

    internal fun getDependencies(
        loadedServices: Map<KClass<out Service>, ServiceCreation>,
        dependencies: Sequence<DependencyDeclaration<*>>
    ): ServiceDependencies {
        val dependencyList = dependencies.fold(mutableListOf<DependencyInstance>()) { list, item ->
            val depType = item.type
            val dependency = requireNotNull(loadedServices[depType]) { "Dependency of type $depType not found. Check for missing factories or cyclic dependencies." }
            list.add(DependencyInstance(item.alias, dependency))
            list
        }
        return ServiceDependencies(dependencyList.asSequence())
    }

}

internal typealias ServiceCreation = () -> Service

class SingletonService(private val service: Service): ServiceCreation {
    override fun invoke(): Service {
        return service
    }

}

data class ServiceBootstrapConfiguration(
    val type: KClass<out ServiceFactory<*>>,
    val locality: ServiceLocality,
    val enabled: Boolean
)

data class LoadedServices(private val serviceMap: Map<KClass<out Service>, ServiceCreation>)

enum class ServiceLocality {
    LOCAL, REMOTE
}
