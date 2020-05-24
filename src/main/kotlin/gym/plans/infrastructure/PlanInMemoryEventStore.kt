package gym.plans.infrastructure

import common.AggregateHistory
import common.AggregateId
import common.DomainEvent
import gym.plans.domain.Plan
import gym.plans.domain.PlanEvent
import gym.plans.domain.PlanEventStore
import gym.plans.domain.PlanId
import java.util.*

class PlanInMemoryEventStore : PlanEventStore {

    private val events = mutableMapOf<PlanId, MutableList<PlanEvent>>()

    override fun nextId(): String {
        return UUID.randomUUID().toString()
    }

    override fun store(events: List<DomainEvent>) {
        events.forEach {
            this.events.getOrPut(PlanId(it.aggregateId())) { mutableListOf() }.add(it as PlanEvent)
        }
    }

    override fun getAggregateHistoryFor(aggregateId: AggregateId): AggregateHistory {
        return AggregateHistory(
            aggregateId,
            getAggregateEvents(aggregateId)
        )
    }

    override fun get(planId: PlanId): Plan {
        return Plan.restoreFrom(getAggregateHistoryFor(planId))
    }

    private fun getAggregateEvents(id: AggregateId): MutableList<PlanEvent> =
        this.events.getOrDefault(id as PlanId, mutableListOf())
}
