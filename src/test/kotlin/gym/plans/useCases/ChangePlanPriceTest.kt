package gym.plans.useCases

import gym.plans.domain.NewPlanCreated
import gym.plans.domain.PlanPriceChanged
import gym.plans.infrastructure.InMemoryPlanEventStore
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.collections.shouldEndWith

class ChangePlanPriceTest : AnnotationSpec() {

    @Test
    fun handle() {
        val eventStore = InMemoryPlanEventStore()
        val planId = eventStore.nextId()

        eventStore.storeEvents(
            listOf(
                NewPlanCreated(planId, 450, 12)
            )
        )

        val tested = ChangePlanPrice(eventStore)

        val events = tested.handle(
            ChangePriceOfPlanCommand(planId, 400)
        )

        events.shouldEndWith(
            PlanPriceChanged(planId, 450, 400)
        )
    }
}
