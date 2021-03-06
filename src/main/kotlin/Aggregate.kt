abstract class Aggregate(
    internal val events: MutableList<DomainEvent> = mutableListOf()
) {
    internal abstract fun getId(): String

    internal fun recentEvents(): List<DomainEvent> {
        val recentEvents = events.toList()
        events.clear()

        return recentEvents
    }

    protected fun applyChange(event: DomainEvent) {
        whenEvent(event)
        events.add(event)
    }

    protected abstract fun whenEvent(event: DomainEvent)
}
