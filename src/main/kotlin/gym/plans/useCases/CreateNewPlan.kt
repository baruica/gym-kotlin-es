package gym.plans.useCases

import DomainEvent
import Id
import gym.plans.domain.Plan
import gym.plans.domain.PlanEventStore

data class CreateNewPlan(
    val planId: String,
    val planPrice: Int,
    val planDurationInMonths: Int,
) {
    class Handler(private val eventStore: PlanEventStore) {

        operator fun invoke(command: CreateNewPlan): List<DomainEvent> {

            return Plan.new(
                Id(command.planId),
                command.planPrice,
                command.planDurationInMonths
            )
                .also { eventStore.store(it) }
                .events
        }
    }
}
