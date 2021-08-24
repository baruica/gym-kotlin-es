package gym.plans.useCases

import DomainEvent
import gym.plans.domain.PlanEventStore

class ChangePlanPrice(private val eventStore: PlanEventStore) {

    operator fun invoke(command: ChangePriceOfPlanCommand): List<DomainEvent> {

        val plan = eventStore.get(command.planId)

        plan.changePrice(command.newPrice)

        eventStore.store(plan)

        return plan.recentEvents()
    }
}
