package gym.plans.useCases

import gym.plans.domain.NewPlanCreated
import gym.plans.infrastructure.InMemoryPlanEventStore
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.collections.shouldEndWith
import io.kotest.matchers.collections.shouldHaveSize

class CreateNewPlanTest : AnnotationSpec() {

    @Test
    fun handle() {
        val eventStore = InMemoryPlanEventStore()
        val planId = eventStore.nextId()

        shouldThrow<IllegalArgumentException> {
            eventStore.get(planId)
        }

        val tested = CreateNewPlan(eventStore)

        val events = tested(CreateNewPlanCommand(planId, 300, 1))

        events.shouldHaveSize(1)
        events.shouldEndWith(
            NewPlanCreated(
                planId,
                300,
                1
            )
        )
    }
}
