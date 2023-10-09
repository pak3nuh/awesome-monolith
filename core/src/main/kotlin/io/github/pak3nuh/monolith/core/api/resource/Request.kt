package io.github.pak3nuh.monolith.core.api.resource

data class Request(
    val path: String,
    val parameters: Map<String, String>,
    val headers: Map<String, String>,
    val body: Body?
) {
    fun acceptHeader(): String {
        return requiredHeader("Accept")
    }

    fun getResourceId(): String {
        return requiredParameter("id")
    }

    private fun requiredHeader(name: String): String {
        val header = headers[name] ?: headers[name.lowercase()]
        return requireNotNull(header) { "Required $name header" }
    }

    private fun requiredParameter(name: String): String {
        val header = parameters[name] ?: parameters[name.lowercase()]
        return requireNotNull(header) { "Required $name parameter" }
    }
}