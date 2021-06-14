package gym.plans.domain

import common.EventStore

interface PlanEventStore : EventStore<Plan> {

    fun get(planId: String): Plan
}
