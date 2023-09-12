package io.github.pak3nuh.monolith.service.user

import io.github.pak3nuh.monolith.core.service.Service

interface UserService: Service {
    fun getUser(id: Id): User?
}

data class User(val id: Id, val name: String)

data class Id(private val innerId: String)
