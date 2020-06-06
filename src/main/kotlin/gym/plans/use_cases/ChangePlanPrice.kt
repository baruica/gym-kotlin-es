package gym.plans.use_cases

import common.DomainEvent
import gym.plans.domain.PlanEventStore
import gym.plans.domain.PlanId

class ChangePlanPrice(private val eventStore: PlanEventStore) {

    fun handle(command: ChangePriceOfPlanCommand): List<DomainEvent> {

        val plan = eventStore.get(PlanId(command.planId))

        plan.changePrice(command.newPrice)

        eventStore.store(plan)

        return plan.occuredEvents()
    }
}
