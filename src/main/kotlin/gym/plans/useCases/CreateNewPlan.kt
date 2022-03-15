package gym.plans.useCases

import DomainEvent
import gym.plans.domain.Plan
import gym.plans.domain.PlanEventStore
import gym.plans.domain.PlanId

data class CreateNewPlan(
    val planId: PlanId,
    val planPrice: Int,
    val planDurationInMonths: Int,
) {
    class Handler(private val eventStore: PlanEventStore) {

        operator fun invoke(command: CreateNewPlan): List<DomainEvent> {

            val aggregateResult = Plan.new(
                command.planId,
                command.planPrice,
                command.planDurationInMonths
            )

            eventStore.store(aggregateResult)

            return aggregateResult.events
        }
    }
}
