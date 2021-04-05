package gym.plans.useCases

import gym.plans.domain.NewPlanCreated
import gym.plans.domain.PlanId
import gym.plans.domain.PlanPriceChanged
import gym.plans.infrastructure.InMemoryPlanEventStore
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ChangePlanPriceTest {

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

        assertEquals(
            PlanPriceChanged(planId, 450, 400),
            aggregateHistory.events.last()
        )
    }
}
