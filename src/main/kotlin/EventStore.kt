import com.github.guepardoapps.kulid.ULID

interface EventStore<ID, A : Identifiable<ID>, E : DomainEvent> {

    fun nextId(): String = ULID.random()

    fun store(aggregateResult: AggregateResult<ID, A, out E>)

    fun storeEvents(events: List<E>)

    fun getAggregateHistory(aggregateId: ID): AggregateHistory<ID, E>
}
