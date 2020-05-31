package gym.plans.use_cases

import gym.plans.domain.PlanEvent
import gym.plans.domain.PlanEventStore
import gym.plans.domain.PlanId

class ChangePlanPrice(private val eventStore: PlanEventStore) {

    fun handle(command: ChangePriceOfPlanCommand): List<PlanEvent> {

        val plan = eventStore.get(PlanId(command.planId))

        plan.changePrice(command.newPrice)

        eventStore.store(plan.recordedEvents)

        return plan.recordedEvents
    }
}
