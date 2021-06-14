package common

open class AggregateId(private val id: String) {
    override fun toString(): String = id
}

data class AggregateHistory(
    val aggregateId: String,
    val events: List<DomainEvent>
) {
    constructor(
        aggregateId: AggregateId,
        events: List<DomainEvent>
    ) : this(aggregateId.toString(), events)
}

abstract class Aggregate private constructor(val id: AggregateId) {
    constructor(aggregateId: String) : this(AggregateId(aggregateId))

    internal val events: MutableList<DomainEvent> = mutableListOf()

    fun getEvents(): List<DomainEvent> {
        return events
    }

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
