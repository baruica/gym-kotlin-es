package gym.plans.use_cases

import gym.plans.domain.Plan
import gym.plans.domain.PlanEvent
import gym.plans.domain.PlanEventStore

class CreateNewPlan(private val eventStore: PlanEventStore) {

    fun handle(command: CreateNewPlanCommand): List<PlanEvent> {

        val plan = Plan.new(
            command.planId,
            command.planPrice,
            command.planDurationInMonths
        )

        eventStore.store(plan.recordedEvents)

        return plan.recordedEvents
    }
}
