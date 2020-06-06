package common

import java.util.*

interface EventStore {

    fun nextId(): String {
        return UUID.randomUUID().toString()
    }

    fun store(aggregate: Aggregate<out AggregateId>) {
        store(aggregate.events)
    }

    fun store(events: List<DomainEvent>)

    fun getAggregateHistory(aggregateId: AggregateId): AggregateHistory {
        return AggregateHistory(
            aggregateId,
            getAggregateEvents(aggregateId)
        )
    }

    fun getAggregateEvents(aggregateId: AggregateId): List<DomainEvent>
}
