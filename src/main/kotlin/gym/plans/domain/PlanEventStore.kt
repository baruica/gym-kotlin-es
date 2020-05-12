package gym.plans.domain

interface PlanEventStore {

    fun nextId(): PlanId

    fun store(events: List<PlanEvent>)

    fun get(planId: PlanId): Plan

    fun getAllEvents(planId: PlanId): List<PlanEvent>
}
