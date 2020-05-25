package gym.plans.use_cases

import gym.plans.domain.Plan
import gym.plans.domain.PlanEvent
import gym.plans.domain.PlanEventStore

class CreateNewPlan(private val planEventStore: PlanEventStore) {

    fun handle(command: CreateNewPlanCommand): List<PlanEvent> {

        val newPlan = Plan(
            command.planId,
            command.planPrice,
            command.planDurationInMonths
        )

        planEventStore.store(newPlan.recordedEvents)

        return newPlan.recordedEvents
    }
}
