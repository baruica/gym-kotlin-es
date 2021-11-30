package gym.plans.useCases

import DomainEvent
import gym.plans.domain.PlanEventStore

data class ChangePlanPrice(
    val planId: String,
    val newPrice: Int,
)

class ChangePlanPriceHandler(private val eventStore: PlanEventStore) {

    operator fun invoke(command: ChangePlanPrice): List<DomainEvent> {

        val plan = eventStore.get(command.planId)

        val aggregateResult = plan.changePrice(command.newPrice)

        eventStore.store(aggregateResult)

        return aggregateResult.events
    }
}
