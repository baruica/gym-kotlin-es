@JvmInline
value class Id<T>(private val value: T) {
    override fun toString(): String {
        return value.toString()
    }
}

interface Identifiable<T> {
    val id: Id<T>
}
