interface EventStore<AGGREGATE: Aggregate, EVENT: DomainEvent> {

    fun nextId(): String

    fun store(aggregateResult: AggregateResult<AGGREGATE, out EVENT>)

    fun storeEvents(events: List<EVENT>)

    fun getAggregateHistory(aggregateId: String): AggregateHistory<EVENT>
}
