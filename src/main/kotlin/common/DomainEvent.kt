package common

interface DomainEvent : Event {

    fun getAggregateId(): String
}
