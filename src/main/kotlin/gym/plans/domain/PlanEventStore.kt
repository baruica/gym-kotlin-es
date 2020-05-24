package gym.plans.domain

import common.EventStore

interface PlanEventStore : EventStore {

    fun get(planId: PlanId): Plan
}
