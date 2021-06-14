import gym.plans.domain.NewPlanCreated
import gym.plans.domain.Plan
import gym.plans.domain.PlanPriceChanged
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.maps.shouldBeEmpty

internal class InMemoryEventStoreTest : AnnotationSpec() {

    @Test
    fun `events of stored aggregates can be retrieved`() {
        val aggregate = Plan.new("id1", 200, 1)
        aggregate.changePrice(180)

        val tested = InMemoryEventStore<Plan>()
        tested.store(aggregate)

        tested.getAggregateEvents("id1").shouldContainExactly(
            listOf(
                NewPlanCreated("id1", 200, 1),
                PlanPriceChanged("id1", 200, 180)
            )
        )
    }

    @Test
    fun `no events can be retrieved when no agregates are stored`() {
        val tested = InMemoryEventStore<Plan>()

        tested.events.shouldBeEmpty()
    }
}
