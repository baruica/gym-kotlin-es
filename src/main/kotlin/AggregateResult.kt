data class AggregateResult<A, E>(
    val aggregate: A,
    val events: List<E>
) {
    companion object {
        fun <A, E> of(aggregate: A, event: E): AggregateResult<A, E> {
            return AggregateResult(aggregate, listOf(event))
        }

        fun <A, E> of(aggregate: A, events: List<E>): AggregateResult<A, E> {
            return AggregateResult(aggregate, events)
        }
    }
}