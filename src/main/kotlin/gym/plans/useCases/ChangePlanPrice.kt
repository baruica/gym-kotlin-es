package gym.plans.useCases

import DomainEvent
import Id
import gym.plans.domain.PlanEventStore

data class ChangePlanPrice(
    val planId: Id<String>,
    val newPrice: Int,
) {
    class Handler(private val eventStore: PlanEventStore) {

        operator fun invoke(command: ChangePlanPrice): List<DomainEvent> {

            eventStore.get(command.planId.toString())
                .let { plan ->
                    return plan.changePrice(command.newPrice)
                        .also { aggregateResult -> eventStore.store(aggregateResult) }
                        .events
                }
        }
    }
}
