data class AggregateResult<ID, A: Identifiable<ID>, E: DomainEvent>(
    val aggregate: A,
    val events: List<E>
) {
    constructor(aggregate: A, event: E) : this(aggregate, listOf(event))
}

fun <ID, A: Identifiable<ID>, E: DomainEvent>emptyAggregateResult(aggregate: A): AggregateResult<ID, A, E> {
    return AggregateResult(aggregate, listOf())
}
