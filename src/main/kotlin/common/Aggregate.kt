package common

interface AggregateId

data class AggregateHistory(
    val aggregateId: AggregateId,
    val events: List<DomainEvent>
)

abstract class Aggregate<T : Any>(val id: T) {

    internal val events: MutableList<DomainEvent> = mutableListOf()

    fun occuredEvents(): List<DomainEvent> {
        val occuredEvents = events.toMutableList()
        events.clear()

        return occuredEvents
    }

    protected fun applyChange(event: DomainEvent) {
        whenEvent(event)
        events.add(event)
    }

    protected abstract fun whenEvent(event: DomainEvent)
}
