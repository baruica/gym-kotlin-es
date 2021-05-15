package gym.plans.useCases

import gym.plans.domain.NewPlanCreated
import gym.plans.domain.PlanId
import gym.plans.infrastructure.InMemoryPlanEventStore
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldEndWith
import io.kotest.matchers.collections.shouldHaveSize

class CreateNewPlanTest : AnnotationSpec() {

    @Test
    fun handle() {
        val eventStore = InMemoryPlanEventStore()
        val planId = eventStore.nextId()

        eventStore.getAggregateHistory(PlanId(planId)).events.shouldBeEmpty()

        val tested = CreateNewPlan(eventStore)

        tested.handle(CreateNewPlanCommand(planId, 300, 1))

        val aggregateHistory = eventStore.getAggregateHistory(PlanId(planId))

        aggregateHistory.events.shouldHaveSize(1)
        aggregateHistory.events.shouldEndWith(
            NewPlanCreated(
                aggregateHistory.aggregateId.toString(),
                300,
                1
            )
        )
    }
}
