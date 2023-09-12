package io.github.pak3nuh.monolith.service.user

import io.github.pak3nuh.monolith.core.service.DependencyDeclaration
import io.github.pak3nuh.monolith.core.service.Service
import io.github.pak3nuh.monolith.core.service.ServiceDependencies
import io.github.pak3nuh.monolith.core.service.ServiceLocality
import kotlin.reflect.KClass

class RemoteUserServiceFactory: UserServiceFactory {
    override val locality = ServiceLocality.REMOTE
    override val serviceType: KClass<UserService> = UserService::class
    override val dependencies: Sequence<DependencyDeclaration<out Service>>
        get() = sequenceOf()

    override fun create(dependencies: ServiceDependencies): UserService {
        TODO()
    }
}