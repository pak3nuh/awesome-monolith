package io.github.pak3nuh.monolith.core.service

import io.github.pak3nuh.monolith.service.api.ApiService
import io.github.pak3nuh.monolith.service.config.ConfigurationService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files

class ServiceDiscoveryTest{
    @Test
    fun `should bootstrap core services`() {
        val config = Files.createTempFile("config", "properties")
        System.setProperty(ServiceDiscovery.CFG_FILE_PROP, config.toString())
        val loadedServices = ServiceDiscovery.boostrapServices().serviceMap
        assertEquals(2, loadedServices.size)
        assertTrue(loadedServices.containsKey(ConfigurationService::class))
        assertTrue(loadedServices.containsKey(ApiService::class))
    }
}