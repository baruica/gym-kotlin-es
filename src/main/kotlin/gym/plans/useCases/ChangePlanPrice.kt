package gym.plans.useCases

import DomainEvent
import gym.plans.domain.PlanEventStore

class ChangePlanPrice(private val eventStore: PlanEventStore) {

    fun handle(command: ChangePriceOfPlanCommand): List<DomainEvent> {

        val plan = eventStore.get(command.planId)

        plan.changePrice(command.newPrice)

        eventStore.store(plan)

        return plan.recentEvents()
    }
}
