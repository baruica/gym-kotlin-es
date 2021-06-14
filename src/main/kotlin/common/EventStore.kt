package common

interface EventStore<T : Aggregate> {

    fun nextId(): String

    fun storeEvents(events: List<DomainEvent>)

    fun store(aggregate: T)

    fun getAggregateEvents(aggregateId: String): List<DomainEvent>
}
