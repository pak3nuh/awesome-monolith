package io.github.pak3nuh.monolith.service.api.rest

import io.github.pak3nuh.monolith.core.api.resource.Request
import io.github.pak3nuh.monolith.core.api.resource.Response
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class ResourceRegistrationKtTest {

    @Test
    fun `should call all interceptors in order`() {
        fun runTest(listSize: Int) {
            println("Running interceptor test with list size $listSize")
            val handler = ResponseHandler { Response(200.toUInt(), null, null) }
            val listRange = 0 until listSize
            val callList = mutableListOf<Int>()
            val interceptorList = listRange.map {
                RequestInterceptor { request, next ->
                    callList.add(it)
                    next.handle(request)
                }
            }.toList()
            val chainList = requestChain(
                interceptorList,
                handler
            )
            val resp = runBlocking {
                chainList.handle(Request("", emptyMap(), emptyMap(), null))
            }

            assertEquals(200.toUInt(), resp.status)
            assertEquals(listSize, callList.size)
            listRange.forEach {
                assertEquals(it, callList.get(it))
            }
        }

        for (i in (0..10)) {
            runTest(i)
        }
    }
}