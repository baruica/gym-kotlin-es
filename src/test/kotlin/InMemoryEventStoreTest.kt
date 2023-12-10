import gym.membership.domain.Member
import gym.membership.domain.MemberEvent
import gym.plans.domain.NewPlanCreated
import gym.plans.domain.Plan
import gym.plans.domain.PlanEvent
import gym.plans.domain.PlanPriceChanged
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.maps.shouldBeEmpty

internal class InMemoryEventStoreTest : StringSpec({

    "events of stored aggregates can be retrieved" {
        val newPlanResult = Plan.new(Id("id1"), 200, 1)
        val changePriceResult = newPlanResult.aggregate.changePrice(180)

        val tested = InMemoryEventStore<String, Plan, PlanEvent>()
        tested.store(newPlanResult)
        tested.store(changePriceResult)

        tested.getAggregateHistory("id1").events.shouldContainExactly(
            listOf(
                NewPlanCreated("id1", 200, 1),
                PlanPriceChanged("id1", 200, 180)
            )
        )
    }

    "no events can be retrieved when no agregates are stored" {
        val tested = InMemoryEventStore<String, Member, MemberEvent>()

        tested.events.shouldBeEmpty()
    }
})
