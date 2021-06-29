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
