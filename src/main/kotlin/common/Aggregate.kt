package common

interface AggregateId

data class AggregateHistory(
    val aggregateId: AggregateId,
    val events: List<DomainEvent>
)

abstract class Aggregate<T : Any>(val id: T) {

    internal val occuredEvents: MutableList<DomainEvent> = mutableListOf()

    fun occuredEvents(): List<DomainEvent> {
        val events = occuredEvents.toMutableList()
        occuredEvents.clear()

        return events
    }

    protected fun applyChange(event: DomainEvent) {
        whenEvent(event)
        occuredEvents.add(event)
    }

    protected abstract fun whenEvent(event: DomainEvent)
}
