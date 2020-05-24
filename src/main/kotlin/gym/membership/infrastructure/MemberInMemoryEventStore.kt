package gym.membership.infrastructure

import common.AggregateHistory
import common.AggregateId
import common.DomainEvent
import gym.membership.domain.MemberEvent
import gym.membership.domain.MemberEventStore
import gym.membership.domain.MemberId
import java.util.*

class MemberInMemoryEventStore : MemberEventStore {

    private val events = mutableMapOf<MemberId, MutableList<MemberEvent>>()

    override fun nextId(): String {
        return UUID.randomUUID().toString()
    }

    override fun store(events: List<DomainEvent>) {
        events.forEach {
            this.events.getOrPut(MemberId(it.aggregateId())) { mutableListOf() }.add(it as MemberEvent)
        }
    }

    override fun getAggregateHistoryFor(aggregateId: AggregateId): AggregateHistory {
        return AggregateHistory(
            aggregateId,
            getAggregateEvents(aggregateId)
        )
    }

    private fun getAggregateEvents(id: AggregateId): MutableList<MemberEvent> =
        this.events.getOrDefault(id as MemberId, mutableListOf())
}
