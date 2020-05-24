package common

interface EventStore {

    fun nextId(): String

    fun store(events: List<DomainEvent>)

    fun getAggregateHistoryFor(aggregateId: AggregateId): AggregateHistory
}
