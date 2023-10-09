package io.github.pak3nuh.monolith.core.api.resource

import com.google.gson.Gson
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.lang.Appendable
import kotlin.reflect.KClass

class JsonBodyMapper<T: Any>(private val type: KClass<T>) {

    fun supports(request: Request): Boolean {
        return request.acceptHeader() == MimeTypes.JSON
    }

    fun toObject(request: Request): T {
        val body = requireNotNull(request.body)
        return body.stream.use {
            jsonMapper.fromJson(InputStreamReader(it), type.java)
        }
    }

    fun toJson(data: T): String {
        return jsonMapper.toJson(data)
    }

    fun toJsonArray(data: List<T>): String {
        val joined = data.asSequence().map { toJson(it) }
            .joinToString(",")
        return "[$joined]"
    }

    fun append(data: T, appendable: Appendable) {
        jsonMapper.toJson(data, appendable)
    }

    private companion object {
        private val jsonMapper = Gson()
    }

}