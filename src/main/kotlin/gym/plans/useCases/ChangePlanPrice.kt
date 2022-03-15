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

            val plan = eventStore.get(command.planId)

            val aggregateResult = plan.changePrice(command.newPrice)

            eventStore.store(aggregateResult)

            return aggregateResult.events
        }
    }
}
