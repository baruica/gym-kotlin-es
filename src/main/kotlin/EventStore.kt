interface EventStore {

    fun nextId(): String

    fun store(aggregateResult: AggregateResult<out Aggregate, out DomainEvent>)

    fun storeEvents(events: List<DomainEvent>)

    fun getAggregateHistory(aggregateId: String): AggregateHistory
}
