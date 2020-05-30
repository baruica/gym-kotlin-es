package common

interface AggregateId

data class AggregateHistory(
    val aggregateId: AggregateId,
    val events: List<DomainEvent>
)
