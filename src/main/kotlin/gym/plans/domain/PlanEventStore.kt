package gym.plans.domain

import EventStore

interface PlanEventStore : EventStore<String, Plan, PlanEvent> {

    fun get(planId: String): Plan
}
