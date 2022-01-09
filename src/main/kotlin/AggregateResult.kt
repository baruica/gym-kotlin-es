data class AggregateResult<AGGREGATE, EVENT>(
    val aggregate: AGGREGATE,
    val events: List<EVENT>
) {
    companion object {
        fun <AGGREGATE, EVENT> of(aggregate: AGGREGATE, event: EVENT): AggregateResult<AGGREGATE, EVENT> {
            return AggregateResult(aggregate, listOf(event))
        }

        fun <AGGREGATE, EVENT> empty(aggregate: AGGREGATE): AggregateResult<AGGREGATE, EVENT> {
            return AggregateResult(aggregate, listOf())
        }
    }
}
