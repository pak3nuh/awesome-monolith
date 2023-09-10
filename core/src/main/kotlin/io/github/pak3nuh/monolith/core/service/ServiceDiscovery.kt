package io.github.pak3nuh.monolith.core.service

import java.util.*
import kotlin.reflect.KClass

object ServiceDiscovery {

    fun loadServices(): LoadedServices {
        val factoryLoader: ServiceLoader<ServiceFactory<*>> = ServiceLoader.load(ServiceFactory::class.java)
        val factories: Sequence<ServiceFactory<*>> = factoryLoader.asSequence()
        val config = mergeConfiguration(getDefaultConfiguration(), getUserConfiguration())
        val filteredLocality = filerLocality(config, factories)
        val services = loadServices(filteredLocality)
        return LoadedServices(services)
    }

    fun getDefaultConfiguration(): Sequence<ServiceBootstrapConfiguration> {
        TODO()
    }

    fun getUserConfiguration(): Sequence<ServiceBootstrapConfiguration> {
        TODO()
    }

    fun mergeConfiguration(
        config: Sequence<ServiceBootstrapConfiguration>, 
        overrides: Sequence<ServiceBootstrapConfiguration>
    ): Sequence<ServiceBootstrapConfiguration> {
        TODO()    
    }

    fun filerLocality(config: Sequence<ServiceBootstrapConfiguration>, factories: Sequence<ServiceFactory<*>>): Sequence<ServiceFactory<*>> {
        val serviceMap = config.associateBy { it.type }
        return factories
            .filter { it: ServiceFactory<*> ->
                val factoryType = it::class
                val serviceConfig = requireNotNull(serviceMap[factoryType]) { "There is no configuration for factory $factoryType" }
                serviceConfig.locality == it.locality
             }
    }

    fun loadServices(factories: Sequence<ServiceFactory<*>>): Map<KClass<out Service>, Service> {
        val loadedServices = mutableMapOf<KClass<out Service>, Service>()
        factories.sortedBy { it.dependencies.count() }
            .onEach { checkExportedTypes(it.serviceType, it.exportedTypes()) }
            .forEach { factory ->
                val dependencies: ServiceDependencies = getDependencies(loadedServices, factory.dependencies)
                val serviceInstance = factory.create(dependencies)
                factory.exportedTypes()
                    .onEach { require(!loadedServices.containsKey(it)) { "Service $it already loaded" } }
                    .forEach { loadedServices[it] = serviceInstance }
            }
        return loadedServices
    }

    private fun getDependencies(
        loadedServices: Map<KClass<out Service>, Service>,
        dependencies: Sequence<DependencyDeclaration<*>>
    ): ServiceDependencies {
        //todo check inter dependencies
        val dependencyList = dependencies.fold(mutableListOf<DependencyInstance>()) { list, item ->
            val depType = item.type
            val service = requireNotNull(loadedServices[depType]) { "Dependency of type $depType not found" }
            list.add(DependencyInstance(item.alias) { service })
            list
        }
        return ServiceDependencies(dependencyList.asSequence())
    }

    private fun checkExportedTypes(serviceType: KClass<out Service>, exportedTypes: Sequence<KClass<out Service>>) {
        require(exportedTypes.count() > 0) { "Service $serviceType must export at least one type" }
        exportedTypes
            .forEach { require(it.java.isAssignableFrom(it.java)) { "Exported type $it is not assignable from $serviceType" } }
    }

}

data class ServiceBootstrapConfiguration(val type: KClass<out ServiceFactory<*>>, val locality: ServiceLocality)

data class LoadedServices(private val serviceMap: Map<KClass<out Service>, Service>)

enum class ServiceLocality {
    LOCAL, REMOTE
}
