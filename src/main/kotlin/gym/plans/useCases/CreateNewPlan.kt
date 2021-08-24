package gym.plans.useCases

import DomainEvent
import gym.plans.domain.Plan
import gym.plans.domain.PlanEventStore

class CreateNewPlan(private val eventStore: PlanEventStore) {

    operator fun invoke(command: CreateNewPlanCommand): List<DomainEvent> {

        val plan = Plan.new(
            command.planId,
            command.planPrice,
            command.planDurationInMonths
        )

        eventStore.store(plan)

        return plan.recentEvents()
    }
}
