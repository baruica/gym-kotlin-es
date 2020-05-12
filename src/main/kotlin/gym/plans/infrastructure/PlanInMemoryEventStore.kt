package gym.plans.infrastructure

import gym.plans.domain.Plan
import gym.plans.domain.PlanEvent
import gym.plans.domain.PlanEventStore
import gym.plans.domain.PlanId
import java.util.*

class PlanInMemoryEventStore : PlanEventStore {

    private val events = HashMap<PlanId, MutableList<PlanEvent>>()

    override fun nextId(): PlanId {
        return PlanId(UUID.randomUUID().toString())
    }

    override fun store(events: List<PlanEvent>) {
        events.forEach {
            this.events.getOrPut(PlanId(it.planId)) { mutableListOf() }.add(it)
        }
    }

    override fun getAllEvents(planId: PlanId): List<PlanEvent> {
        return getAggregateEvents(planId)
    }

    override fun get(planId: PlanId): Plan {
        return Plan.restoreFrom(getAggregateEvents(planId))
    }

    private fun getAggregateEvents(id: PlanId): MutableList<PlanEvent> = this.events.getOrDefault(id, mutableListOf())
}
