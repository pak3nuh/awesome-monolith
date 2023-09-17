package io.github.pak3nuh.monolith.service.config

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class PropsNodeImplTest {
    private val props = mapOf(
        Pair("timeout", "50"),
        Pair("a.b.c.d.e", "benfica"),
        Pair("core.services.user.enabled", "true"),
        Pair("core.services.api.enabled", "true"),
        Pair("core.services.api.locality", "LOCAL")
    )
    private val config = PropsNodeImpl(props, ".")

    @Test
    fun `should create simple value`() {
        val value = config.value("timeout")
        assertEquals("50", value)
    }

    @Test
    fun `should traverse nodes`() {
        val node = config.node("a", "b", "c", "d")
        assertEquals("benfica", node.value("e"))
    }

    @Test
    fun `should create map`() {
        val nodeMap = config.node("core").map("services")
        assertEquals(2, nodeMap.size)
        assertTrue(nodeMap.containsKey("api"))
        assertTrue(nodeMap.containsKey("user"))
        val node = requireNotNull(nodeMap["api"])
        assertEquals("true", node.value("enabled"))
    }
}