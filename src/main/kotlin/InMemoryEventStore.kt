import com.github.guepardoapps.kulid.ULID

open class InMemoryEventStore<AGGREGATE: Aggregate, EVENT: DomainEvent>(
    val events: MutableMap<String, MutableList<EVENT>> = mutableMapOf()
) : EventStore<AGGREGATE, EVENT> {

    override fun nextId(): String = ULID.random()

    override fun store(aggregateResult: AggregateResult<AGGREGATE, out EVENT>) {
        this.events.getOrPut(aggregateResult.aggregate.getId()) { mutableListOf() }.addAll(aggregateResult.events)
    }

    override fun storeEvents(events: List<EVENT>) {
        events.forEach {
            this.events.getOrPut((it as DomainEvent).getAggregateId()) { mutableListOf() }.add(it)
        }
    }

    override fun getAggregateHistory(aggregateId: String): AggregateHistory<EVENT> {
        return AggregateHistory(
            aggregateId,
            this.events.getOrDefault(aggregateId, mutableListOf())
        )
    }
}
