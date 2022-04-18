import gym.membership.domain.Member
import gym.membership.domain.MemberEvent
import gym.plans.domain.*
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.maps.shouldBeEmpty

internal class InMemoryEventStoreTest : AnnotationSpec() {

    @Test
    fun `events of stored aggregates can be retrieved`() {
        val newPlanResult = Plan.new(PlanId("id1"), 200, 1)
        val changePriceResult = newPlanResult.aggregate.changePrice(180)

        val tested = InMemoryEventStore<Plan, PlanEvent>()
        tested.store(newPlanResult)
        tested.store(changePriceResult)

        tested.getAggregateHistory("id1").events.shouldContainExactly(
            listOf(
                NewPlanCreated("id1", 200, 1),
                PlanPriceChanged("id1", 200, 180)
            )
        )
    }

    @Test
    fun `no events can be retrieved when no agregates are stored`() {
        val tested = InMemoryEventStore<Member, MemberEvent>()

        tested.events.shouldBeEmpty()
    }
}
