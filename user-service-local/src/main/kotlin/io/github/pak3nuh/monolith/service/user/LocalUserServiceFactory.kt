package io.github.pak3nuh.monolith.service.user

import io.github.pak3nuh.monolith.core.service.DependencyDeclaration
import io.github.pak3nuh.monolith.core.service.Service
import io.github.pak3nuh.monolith.core.service.ServiceDependencies
import kotlin.reflect.KClass

class LocalUserServiceFactory: UserServiceFactory {
    override val serviceType: KClass<UserService>
        get() = UserService::class
    override val dependencies: Sequence<DependencyDeclaration<out Service>>
        get() = sequenceOf()

    override fun create(dependencies: ServiceDependencies): UserService {
        return LocalUserService()
    }
}