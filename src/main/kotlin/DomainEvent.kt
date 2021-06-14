interface DomainEvent : Event {

    fun getAggregateId(): String
}
