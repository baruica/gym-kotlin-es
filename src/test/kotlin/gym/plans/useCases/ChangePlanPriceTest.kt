package gym.plans.useCases

import Id
import gym.plans.domain.NewPlanCreated
import gym.plans.domain.PlanPriceChanged
import gym.plans.infrastructure.InMemoryPlanEventStore
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldEndWith

class ChangePlanPriceTest : StringSpec({

    "handle" {
        val eventStore = InMemoryPlanEventStore()
        val planId = eventStore.nextId()

        eventStore.storeEvents(
            listOf(
                NewPlanCreated(planId, 450, 12)
            )
        )

        val tested = ChangePlanPrice.Handler(eventStore)

        val events = tested(
            ChangePlanPrice(Id(planId), 400)
        )

        events.shouldEndWith(
            PlanPriceChanged(planId, 450, 400)
        )
    }
})
