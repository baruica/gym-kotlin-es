package gym.plans.use_cases

import gym.plans.domain.PlanEvent
import gym.plans.domain.PlanEventStore
import gym.plans.domain.PlanId

class ChangePlanPrice(private val planEventStore: PlanEventStore) {

    fun handle(command: ChangePriceOfPlanCommand): List<PlanEvent> {

        val plan = planEventStore.get(PlanId(command.planId))

        plan.changePrice(command.newPrice)

        planEventStore.store(plan.history)

        return plan.history
    }
}
