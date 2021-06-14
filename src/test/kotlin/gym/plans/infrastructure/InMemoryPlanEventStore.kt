package gym.plans.infrastructure

import InMemoryEventStore
import common.AggregateHistory
import gym.plans.domain.Plan
import gym.plans.domain.PlanEventStore

class InMemoryPlanEventStore : InMemoryEventStore<Plan>(), PlanEventStore {

    override fun get(planId: String): Plan {
        return Plan.restoreFrom(
            AggregateHistory(
                planId,
                getAggregateEvents(planId)
            )
        )
    }
}
