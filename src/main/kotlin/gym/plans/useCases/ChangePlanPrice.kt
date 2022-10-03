package gym.plans.useCases

import DomainEvent
import gym.plans.domain.PlanEventStore
import gym.plans.domain.PlanId

data class ChangePlanPrice(
    val planId: PlanId,
    val newPrice: Int,
) {
    class Handler(private val eventStore: PlanEventStore) {

        operator fun invoke(command: ChangePlanPrice): List<DomainEvent> {

            eventStore.get(command.planId)
                .let { plan ->
                    return plan.changePrice(command.newPrice)
                        .also { aggregateResult -> eventStore.store(aggregateResult) }
                        .events
                }
        }
    }
}
