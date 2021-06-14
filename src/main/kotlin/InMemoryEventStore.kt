import common.Aggregate
import common.DomainEvent
import common.EventStore
import java.util.*

open class InMemoryEventStore<T : Aggregate> : EventStore<T> {

    val events: MutableMap<String, MutableList<DomainEvent>> = mutableMapOf()

    override fun nextId(): String = UUID.randomUUID().toString()

    override fun store(aggregate: T) = storeEvents(aggregate.getEvents())

    override fun storeEvents(events: List<DomainEvent>) {
        events.forEach {
            this.events.getOrPut(it.getAggregateId()) { mutableListOf() }.add(it)
        }
    }

    override fun getAggregateEvents(aggregateId: String): List<DomainEvent> =
        this.events.getOrDefault(aggregateId, mutableListOf())
}
