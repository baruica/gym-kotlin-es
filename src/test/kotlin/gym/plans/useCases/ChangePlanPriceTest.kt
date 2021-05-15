package gym.plans.useCases

import gym.plans.domain.NewPlanCreated
import gym.plans.domain.PlanId
import gym.plans.domain.PlanPriceChanged
import gym.plans.infrastructure.InMemoryPlanEventStore
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.collections.shouldEndWith

class ChangePlanPriceTest : AnnotationSpec() {

    @Test
    fun handle() {
        val eventStore = InMemoryPlanEventStore()
        val planId = eventStore.nextId()

        eventStore.store(
            listOf(
                NewPlanCreated(planId, 450, 12)
            )
        )

        val tested = ChangePlanPrice(eventStore)

        tested.handle(
            ChangePriceOfPlanCommand(planId, 400)
        )

        val aggregateHistory = eventStore.getAggregateHistory(PlanId(planId))

        aggregateHistory.events.shouldEndWith(
            PlanPriceChanged(planId, 450, 400)
        )
    }
}
