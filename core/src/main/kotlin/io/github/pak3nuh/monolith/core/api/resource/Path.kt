package io.github.pak3nuh.monolith.core.api.resource

class Path private constructor(private val segments: Collection<Segment>) {
    override fun toString(): String {
        return segments.joinToString("/")
    }

    class Builder {
        private val segments = ArrayList<Segment>()

        fun path(value: String): Builder {
            segments.add(Segment(value, false))
            return this
        }

        fun param(value: String): Builder {
            segments.add(Segment(value, true))
            return this
        }

        fun build(): Path = Path(segments)
    }

    companion object {
        fun builder(): Builder = Builder()
    }
}

private class Segment(private val value: String, private val isParam: Boolean) {
    override fun toString(): String {
        return if (isParam) {
            "{$value}"
        } else {
            value
        }
    }
}
