package gym.plans.useCases

import gym.plans.domain.NewPlanCreated
import gym.plans.domain.PlanId
import gym.plans.infrastructure.InMemoryPlanEventStore
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CreateNewPlanTest {

    @Test
    fun handle() {
        val eventStore = InMemoryPlanEventStore()
        val planId = eventStore.nextId()

        assertEquals(0, eventStore.getAggregateHistory(PlanId(planId)).events.size)

        val tested = CreateNewPlan(eventStore)

        tested.handle(CreateNewPlanCommand(planId, 300, 1))

        val aggregateHistory = eventStore.getAggregateHistory(PlanId(planId))

        assertEquals(1, aggregateHistory.events.size)
        assertEquals(
            NewPlanCreated(
                aggregateHistory.aggregateId.toString(),
                300,
                1
            ),
            aggregateHistory.events.last()
        )
    }
}
