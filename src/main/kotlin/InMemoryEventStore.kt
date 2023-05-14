open class InMemoryEventStore<ID, A: Identifiable<ID>, E: DomainEvent>(
    val events: MutableMap<String, MutableList<E>> = mutableMapOf()
) : EventStore<ID, A, E> {

    override fun store(aggregateResult: AggregateResult<ID, A, out E>) {
        this.events.getOrPut(aggregateResult.aggregate.id.toString()) { mutableListOf() }.addAll(aggregateResult.events)
    }

    override fun storeEvents(events: List<E>) {
        events.forEach {
            this.events.getOrPut(it.getAggregateId()) { mutableListOf() }.add(it)
        }
    }

    override fun getAggregateHistory(aggregateId: ID): AggregateHistory<ID, E> {
        return AggregateHistory(
            Id(aggregateId),
            this.events.getOrDefault(aggregateId, mutableListOf())
        )
    }
}
