package gym.plans.useCases

import gym.plans.domain.Plan
import gym.plans.domain.PlanEvent
import gym.plans.domain.PlanEventStore

data class CreateNewPlanCommand(
    val planId: String,
    val planPrice: Int,
    val planDurationInMonths: Int,
)

class CreateNewPlan(private val eventStore: PlanEventStore) {

    operator fun invoke(command: CreateNewPlanCommand): PlanEvent {

        val plan = Plan.new(
            command.planId,
            command.planPrice,
            command.planDurationInMonths
        )

        eventStore.store(plan)

        return plan.event as PlanEvent
    }
}
