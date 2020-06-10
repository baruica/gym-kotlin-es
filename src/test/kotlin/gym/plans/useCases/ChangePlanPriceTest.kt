package gym.plans.useCases

import gym.plans.domain.NewPlanCreated
import gym.plans.domain.PlanId
import gym.plans.domain.PlanPriceChanged
import gym.plans.infrastructure.PlanInMemoryEventStore
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ChangePlanPriceTest {

    @Test
    fun handle() {
        val planEventStore = PlanInMemoryEventStore()
        val planId = planEventStore.nextId()

        planEventStore.store(listOf(
            NewPlanCreated(planId, 450, 12)
        ))

        val tested = ChangePlanPrice(planEventStore)

        tested.handle(
            ChangePriceOfPlanCommand(planId, 400)
        )

        val aggregateHistory = planEventStore.getAggregateHistory(PlanId(planId))

        assertEquals(
            aggregateHistory.events.last(),
            PlanPriceChanged(planId, 450, 400)
        )
    }
}
