package io.github.pak3nuh.monolith.service.user

internal class LocalUserService(private val dbUrl: String) : UserService {
    override fun getUsers(): List<User> {
        TODO("Not yet implemented")
    }

    override fun close() {
    }
}
