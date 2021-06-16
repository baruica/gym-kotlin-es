abstract class Aggregate(
    internal val events: MutableList<DomainEvent> = mutableListOf()
) {
    internal abstract fun getId(): String

    protected fun applyChange(event: DomainEvent) {
        whenEvent(event)
        events.add(event)
    }

    protected abstract fun whenEvent(event: DomainEvent)

    fun recentEvents(): List<DomainEvent> {
        val recentEvents = events.toList()
        events.clear()

        return recentEvents
    }
}

data class AggregateHistory(
    val aggregateId: String,
    val events: List<DomainEvent>
) {
    init {
        require(events.isNotEmpty()) {
            "No events, no history."
        }
    }
}
