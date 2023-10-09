package io.github.pak3nuh.monolith.funtional

class Or<T, U> private constructor(
    private val first: T?,
    private val second: U?
){

    fun <R> fold(
        firstMapper: (T) -> R,
        secondMapper: (U) -> R,
    ): R {
        return first?.let(firstMapper::invoke)
            ?: second?.let(secondMapper::invoke)
            ?: error("Shouldn't be here")
    }

    suspend fun <R> foldSuspending(
        firstMapper: suspend (T) -> R,
        secondMapper: suspend (U) -> R,
    ): R {
        return if (first != null) {
            firstMapper(first)
        } else if (second != null) {
            secondMapper(second)
        } else {
            error("Shouldn't be here")
        }
    }

    companion object {
        fun <T, R> ofFirst(first: T) = Or<T, R>(first, null)
        fun <T, R> ofSecond(second: R) = Or<T, R>(null, second)
    }
}