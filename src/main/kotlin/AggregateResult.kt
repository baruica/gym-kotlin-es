data class AggregateResult<AGGREGATE: Aggregate, EVENT: DomainEvent>(
    val aggregate: AGGREGATE,
    val events: List<EVENT>
) {
    companion object {
        fun <AGGREGATE: Aggregate, EVENT: DomainEvent> of(aggregate: AGGREGATE, event: EVENT): AggregateResult<AGGREGATE, EVENT> {
            return AggregateResult(aggregate, listOf(event))
        }

        fun <AGGREGATE: Aggregate, EVENT: DomainEvent> empty(aggregate: AGGREGATE): AggregateResult<AGGREGATE, EVENT> {
            return AggregateResult(aggregate, listOf())
        }
    }
}
