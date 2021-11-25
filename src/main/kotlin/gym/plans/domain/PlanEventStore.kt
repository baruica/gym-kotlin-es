package gym.plans.domain

import EventStore

interface PlanEventStore : EventStore {

    fun get(planId: String): Plan
}
