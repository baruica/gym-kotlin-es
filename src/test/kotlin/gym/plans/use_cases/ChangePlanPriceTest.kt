package gym.plans.use_cases

import gym.plans.domain.NewPlanCreated
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
            NewPlanCreated(planId.toString(), 450, 12)
        ))

        val tested = ChangePlanPrice(planEventStore)

        tested.handle(
            ChangePriceOfPlanCommand(planId.toString(), 400)
        )

        val events = planEventStore.getAllEvents(planId)

        assertEquals(
            events.last(),
            PlanPriceChanged(planId.toString(), 450, 400)
        )
    }
}
