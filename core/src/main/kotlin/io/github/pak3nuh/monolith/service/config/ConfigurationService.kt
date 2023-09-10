package io.github.pak3nuh.monolith.service.config

import io.github.pak3nuh.monolith.core.service.Service

interface ConfigurationService: Service

internal class ConfigurationServiceImpl: ConfigurationService {

    override fun close() { }

}