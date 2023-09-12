package io.github.pak3nuh.monolith.service.user

internal class LocalUserService(private val dbUrl: String) : UserService {
    override fun close() {
    }
}
