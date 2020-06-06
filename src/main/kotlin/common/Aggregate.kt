package common

interface AggregateId

data class AggregateHistory(
    val aggregateId: AggregateId,
    val events: List<DomainEvent>
)

abstract class Aggregate {
    val changes: MutableList<DomainEvent> = mutableListOf()

    protected fun applyChange(event: DomainEvent) {
        whenEvent(event)
        changes.add(event)
    }

    protected abstract fun whenEvent(event: DomainEvent)
}
