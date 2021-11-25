package gym.plans.infrastructure

import InMemoryEventStore
import gym.plans.domain.Plan
import gym.plans.domain.PlanEventStore

class InMemoryPlanEventStore : InMemoryEventStore(), PlanEventStore {

    override fun get(planId: String): Plan {
        return Plan.restoreFrom(
            getAggregateHistory(planId)
        )
    }
}
