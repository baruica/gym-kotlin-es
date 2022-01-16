data class AggregateHistory<EVENT>(
    val aggregateId: String,
    val events: List<EVENT>
) {
    init {
        require(events.isNotEmpty()) {
            "No events, no history."
        }
    }
}
