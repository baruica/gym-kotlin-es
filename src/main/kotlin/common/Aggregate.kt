package common

interface AggregateId

interface Aggregate {
    val id: AggregateId
    val raisedEvents: List<DomainEvent>
}

data class AggregateHistory(
    val aggregateId: AggregateId,
    val events: List<DomainEvent>
)
