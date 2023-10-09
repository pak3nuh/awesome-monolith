package io.github.pak3nuh.monolith.core.api.resource

import java.io.InputStream

data class Body(
    val mimeType: String,
    val stream: InputStream
)