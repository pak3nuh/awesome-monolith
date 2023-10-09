package io.github.pak3nuh.monolith.core.api.resource

data class Definition(
    val path: Path,
    val methods: Set<Method>,
    val mimeTypes: Set<String>
)