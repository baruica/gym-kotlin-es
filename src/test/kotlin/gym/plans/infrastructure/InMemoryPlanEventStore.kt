package gym.plans.infrastructure

import InMemoryEventStore
import gym.plans.domain.Plan
import gym.plans.domain.PlanEvent
import gym.plans.domain.PlanEventStore
import gym.plans.domain.PlanId

class InMemoryPlanEventStore : InMemoryEventStore<Plan, PlanEvent>(), PlanEventStore {

    override fun get(planId: PlanId): Plan {
        return Plan.restoreFrom(
            getAggregateHistory(planId.toString())
        )
    }
}
