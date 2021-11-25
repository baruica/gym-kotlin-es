interface EventStore {

    fun nextId(): String

    fun store(aggregateResult: AggregateResult<Aggregate, DomainEvent>)

    fun storeEvents(events: List<DomainEvent>)

    fun getAggregateHistory(aggregateId: String): AggregateHistory
}
