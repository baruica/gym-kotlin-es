package gym.plans.use_cases

import common.DomainEvent
import gym.plans.domain.Plan
import gym.plans.domain.PlanEventStore

class CreateNewPlan(private val eventStore: PlanEventStore) {

    fun handle(command: CreateNewPlanCommand): List<DomainEvent> {

        val plan = Plan.new(
            command.planId,
            command.planPrice,
            command.planDurationInMonths
        )

        eventStore.store(plan.changes)

        return plan.changes
    }
}
