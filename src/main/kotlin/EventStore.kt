interface EventStore<T : Aggregate> {

    fun nextId(): String

    fun store(aggregate: T)

    fun storeEvents(events: List<DomainEvent>)

    fun getAggregateHistory(aggregateId: String): AggregateHistory
}
