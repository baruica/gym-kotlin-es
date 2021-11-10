package gym.plans.useCases

import DomainEvent
import gym.plans.domain.PlanEventStore

data class ChangePlanPriceCommand(
    val planId: String,
    val newPrice: Int,
)

class ChangePlanPrice(private val eventStore: PlanEventStore) {

    operator fun invoke(command: ChangePlanPriceCommand): List<DomainEvent> {

        val plan = eventStore.get(command.planId)

        plan.changePrice(command.newPrice)

        eventStore.store(plan)

        return plan.recentEvents()
    }
}
