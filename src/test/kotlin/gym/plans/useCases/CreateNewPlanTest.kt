package gym.plans.useCases

import gym.plans.domain.NewPlanCreated
import gym.plans.domain.PlanId
import gym.plans.infrastructure.InMemoryPlanEventStore
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CreateNewPlanTest {

    @Test
    fun handle() {
        val planEventStore = InMemoryPlanEventStore()
        val planId = planEventStore.nextId()

        assertEquals(0, planEventStore.getAggregateHistory(PlanId(planId)).events.size)

        val tested = CreateNewPlan(planEventStore)

        tested.handle(CreateNewPlanCommand(planId, 300, 1))

        val aggregateHistory = planEventStore.getAggregateHistory(PlanId(planId))

        assertEquals(1, aggregateHistory.events.size)
        assertEquals(
            aggregateHistory.events.last(),
            NewPlanCreated(
                aggregateHistory.aggregateId.toString(),
                300,
                1
            )
        )
    }
}
