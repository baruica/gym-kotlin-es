package gym.plans.domain

import EventStore

interface PlanEventStore : EventStore<Plan, PlanEvent> {

    fun get(planId: String): Plan
}
