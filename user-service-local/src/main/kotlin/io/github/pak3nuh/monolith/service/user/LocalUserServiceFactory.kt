package io.github.pak3nuh.monolith.service.user

import io.github.pak3nuh.monolith.core.api.resource.*
import io.github.pak3nuh.monolith.core.service.DependencyDeclaration
import io.github.pak3nuh.monolith.core.service.Service
import io.github.pak3nuh.monolith.core.service.ServiceDependencies
import io.github.pak3nuh.monolith.core.service.ServiceLocality
import io.github.pak3nuh.monolith.service.api.ApiService
import io.github.pak3nuh.monolith.service.api.rest.ResourceRegistrar
import io.github.pak3nuh.monolith.service.api.rest.ResponseHandler
import io.github.pak3nuh.monolith.service.config.ConfigurationService
import java.io.ByteArrayInputStream
import kotlin.reflect.KClass

class LocalUserServiceFactory: UserServiceFactory {
    override val locality = ServiceLocality.LOCAL
    override val serviceType: KClass<UserService> = UserService::class
    private val cfgDep = DependencyDeclaration(ConfigurationService::class, "config")
    private val apiDep = DependencyDeclaration(ApiService::class, "api")

    override val dependencies: Sequence<DependencyDeclaration<out Service>> = sequenceOf(cfgDep, apiDep)

    override fun create(dependencies: ServiceDependencies): UserService {
        val cfg = dependencies.getDependency(cfgDep)
        val api = dependencies.getDependency(apiDep)
        val dbUrl = checkNotNull(cfg.node("services", "user", "storage").value("url")) { "DB url is required" }
        val localUserService = LocalUserService(dbUrl)
        registerApiHandlers(localUserService, api.restResource())
        return localUserService
    }

    private fun registerApiHandlers(localUserService: LocalUserService, resourceRegistrar: ResourceRegistrar) {
        val path = Path.builder().path("user").build()
        val mapper = JsonBodyMapper(User::class)
        resourceRegistrar.registerResource(Definition(path, setOf(Method.GET), setOf(MimeTypes.JSON)), emptyList()) {
            val users = localUserService.getUsers()
            val bodyJson = mapper.toJsonArray(users)
            Response(
                200.toUInt(),
                null,
                Body(
                    MimeTypes.JSON,
                    ByteArrayInputStream(bodyJson.toByteArray())
                )
            )
        }
    }
}
