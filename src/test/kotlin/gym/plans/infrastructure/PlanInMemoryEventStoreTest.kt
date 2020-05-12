package gym.plans.infrastructure

import gym.plans.domain.PlanEvent
import gym.plans.domain.PlanEvent.NewPlanCreated
import gym.plans.domain.PlanId
import org.junit.Test
import kotlin.test.assertEquals

class PlanInMemoryEventStoreTest {

    @Test
    fun `what is stores can be retrived`() {
        val tested = PlanInMemoryEventStore()

        val planId1Event1 = NewPlanCreated("planId1", 200, 1)
        val planId1Event2 = PlanEvent.PlanPriceChanged("planId1", 200, 250)
        val planId1Event3 = PlanEvent.PlanPriceChanged("planId1", 250, 300)
        val planId2Event1 = NewPlanCreated("planId2", 100, 1)
        val planId2Event2 = PlanEvent.PlanPriceChanged("planId2", 100, 120)

        tested.store(listOf(
            planId1Event1,
            planId1Event2,
            planId2Event1,
            planId2Event2,
            planId1Event3
        ))

        assertEquals(
            listOf(
                planId1Event1,
                planId1Event2,
                planId1Event3
            ),
            tested.getAllEvents(PlanId("planId1"))
        )

        assertEquals(
            listOf(
                planId2Event1,
                planId2Event2
            ),
            tested.getAllEvents(PlanId("planId2"))
        )
    }
}
