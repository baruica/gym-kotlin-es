data class AggregateResult<T, U>(
    val aggregate: T,
    val event: U
) {
    companion object {
        fun of(
            aggregate: Aggregate,
            event: DomainEvent
        ): AggregateResult<Aggregate, DomainEvent> {
            return AggregateResult(aggregate, event)
        }
    }
}
