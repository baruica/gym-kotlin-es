package gym.plans.domain

import EventStore

interface PlanEventStore : EventStore<Plan> {

    fun get(planId: String): Plan
}
