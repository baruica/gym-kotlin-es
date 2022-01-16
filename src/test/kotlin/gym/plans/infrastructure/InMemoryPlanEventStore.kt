package gym.plans.infrastructure

import InMemoryEventStore
import gym.plans.domain.Plan
import gym.plans.domain.PlanEvent
import gym.plans.domain.PlanEventStore

class InMemoryPlanEventStore : InMemoryEventStore<Plan, PlanEvent>(), PlanEventStore {

    override fun get(planId: String): Plan {
        return Plan.restoreFrom(
            getAggregateHistory(planId)
        )
    }
}
