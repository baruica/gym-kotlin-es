data class AggregateHistory<ID, EVENT>(
    val aggregateId: Id<ID>,
    val events: List<EVENT>
) {
    init {
        require(events.isNotEmpty()) {
            "No events, no history."
        }
    }
}
