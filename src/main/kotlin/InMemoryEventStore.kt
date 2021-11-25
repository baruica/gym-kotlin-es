import java.util.*

open class InMemoryEventStore(
    val events: MutableMap<String, MutableList<DomainEvent>> = mutableMapOf()
) : EventStore {

    override fun nextId(): String = UUID.randomUUID().toString()

    override fun store(aggregateResult: AggregateResult<out Aggregate, out DomainEvent>) {
        this.events.getOrPut(aggregateResult.aggregate.getId()) { mutableListOf() }.addAll(aggregateResult.events)
    }

    override fun storeEvents(events: List<DomainEvent>) {
        events.forEach {
            this.events.getOrPut(it.getAggregateId()) { mutableListOf() }.add(it)
        }
    }

    override fun getAggregateHistory(aggregateId: String): AggregateHistory {
        return AggregateHistory(
            aggregateId,
            this.events.getOrDefault(aggregateId, mutableListOf())
        )
    }
}
