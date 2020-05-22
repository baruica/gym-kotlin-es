package gym.plans.use_cases

import gym.plans.domain.NewPlanCreated
import gym.plans.infrastructure.PlanInMemoryEventStore
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CreateNewPlanTest {

    @Test
    fun handle() {
        val planEventStore = PlanInMemoryEventStore()
        val planId = planEventStore.nextId()

        assertEquals(0, planEventStore.getAllEvents(planId).size)

        val tested = CreateNewPlan(planEventStore)

        tested.handle(CreateNewPlanCommand(planId, 300, 1))

        val events = planEventStore.getAllEvents(planId)

        assertEquals(1, events.size)
        assertEquals(
            events.last(),
            NewPlanCreated(
                events.last().planId,
                300,
                1
            )
        )
    }
}
