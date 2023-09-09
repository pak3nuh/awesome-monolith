package io.github.pak3nuh.monolith.core.service

import java.util.*
import kotlin.reflect.KClass

object ServiceDiscovery {

    fun getAllServiceFactories(config: List<ServiceConfiguration>): Sequence<ServiceFactory<*>> {
        val factoriesLoaded: ServiceLoader<ServiceFactory<*>> = ServiceLoader.load(ServiceFactory::class.java)
        return factoriesLoaded.asSequence()
            .filter { it.locality == config }
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
        loadedServices: MutableMap<KClass<out Service>, Service>,
        dependencies: Sequence<DependencyDeclaration<*>>
    ): ServiceDependencies {
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

data class ServiceConfiguration(val type: KClass<out ServiceFactory<*>>, val locality: ServiceLocality)

enum class ServiceLocality {
    LOCAL, REMOTE
}
