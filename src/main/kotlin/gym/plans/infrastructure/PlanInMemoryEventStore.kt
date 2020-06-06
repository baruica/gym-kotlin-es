package gym.plans.infrastructure

import common.AggregateId
import common.DomainEvent
import gym.plans.domain.Plan
import gym.plans.domain.PlanEvent
import gym.plans.domain.PlanEventStore
import gym.plans.domain.PlanId

class PlanInMemoryEventStore : PlanEventStore {

    private val events = mutableMapOf<PlanId, MutableList<PlanEvent>>()

    override fun store(events: List<DomainEvent>) {
        events.forEach {
            this.events.getOrPut(PlanId(it.aggregateId())) { mutableListOf() }.add(it as PlanEvent)
        }
    }

    override fun get(planId: PlanId): Plan {
        return Plan.restoreFrom(getAggregateHistory(planId))
    }

    override fun getAggregateEvents(aggregateId: AggregateId): MutableList<PlanEvent> =
        this.events.getOrDefault(aggregateId as PlanId, mutableListOf())
}
