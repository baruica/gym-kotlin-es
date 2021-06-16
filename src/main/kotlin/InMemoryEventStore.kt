import java.util.*

open class InMemoryEventStore<T : Aggregate> : EventStore<T> {

    val events: MutableMap<String, MutableList<DomainEvent>> = mutableMapOf()

    override fun nextId(): String = UUID.randomUUID().toString()

    override fun store(aggregate: T) {
        aggregate.events.forEach {
            this.events.getOrPut(aggregate.getId()) { mutableListOf() }.add(it)
        }
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
