package io.github.pak3nuh.monolith.core.service

import kotlin.reflect.KClass
import kotlin.reflect.cast

/**
 * Singleton
 */
interface Service: AutoCloseable

/**
 * No args
 * Singleton
 */
interface ServiceFactory<T: Service> {

    val serviceType: KClass<T>

    val locality: ServiceLocality

    val dependencies: Sequence<DependencyDeclaration<out Service>>

    fun create(dependencies: ServiceDependencies): T
}

data class DependencyDeclaration<T: Service>(val type: KClass<T>, val alias: String)

class ServiceDependencies(private val dependencies: Sequence<DependencyInstance>) {
    fun <T: Service> getDependency(declaration: DependencyDeclaration<T>): T {
        return dependencies.filter { it.alias == declaration.alias }
            .map { declaration.type.cast(it.dependency) }
            .first()
    }
}

data class DependencyInstance(val alias: String, val dependency: () -> Service)

enum class ServiceLocality {
    LOCAL, REMOTE
}
