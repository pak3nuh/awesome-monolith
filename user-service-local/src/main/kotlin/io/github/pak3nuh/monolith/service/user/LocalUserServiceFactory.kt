package io.github.pak3nuh.monolith.service.user

import io.github.pak3nuh.monolith.core.service.DependencyDeclaration
import io.github.pak3nuh.monolith.core.service.Service
import io.github.pak3nuh.monolith.core.service.ServiceDependencies
import io.github.pak3nuh.monolith.core.service.ServiceLocality
import io.github.pak3nuh.monolith.service.config.ConfigurationService
import kotlin.reflect.KClass

class LocalUserServiceFactory: UserServiceFactory {
    override val locality = ServiceLocality.LOCAL
    override val serviceType: KClass<UserService> = UserService::class
    private val cfgDep = DependencyDeclaration(ConfigurationService::class, "config")

    override val dependencies: Sequence<DependencyDeclaration<out Service>> = sequenceOf(cfgDep)

    override fun create(dependencies: ServiceDependencies): UserService {
        val cfg = dependencies.getService(cfgDep)
        val dbUrl = checkNotNull(cfg.node("services", "user", "storage")?.value("url")) { "DB url is required" }
        return LocalUserService(dbUrl)
    }
}