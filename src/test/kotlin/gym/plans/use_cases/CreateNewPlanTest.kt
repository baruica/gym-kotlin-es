package gym.plans.use_cases

import gym.plans.domain.NewPlanCreated
import gym.plans.domain.PlanId
import gym.plans.infrastructure.PlanInMemoryEventStore
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CreateNewPlanTest {

    @Test
    fun handle() {
        val planEventStore = PlanInMemoryEventStore()
        val planId = planEventStore.nextId()

        assertEquals(0, planEventStore.getAggregateHistoryFor(PlanId(planId)).events.size)

        val tested = CreateNewPlan(planEventStore)

        tested.handle(CreateNewPlanCommand(PlanId(planId), 300, 1))

        val aggregateHistory = planEventStore.getAggregateHistoryFor(PlanId(planId))

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
