package gym.plans.domain

import AggregateHistory
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldEndWith
import io.kotest.matchers.shouldBe

class PlanTest : AnnotationSpec() {

    @Test
    fun `a duration cannot be anything but 1 month or 12 months`() {
        shouldThrow<IllegalArgumentException> {
            Plan.new("plan abc", 400, 4)
        }
    }

    @Test
    fun `a price cannot be negative`() {
        shouldThrow<IllegalArgumentException> {
            Plan.new("plan abc", -10, 1)
        }
    }

    @Test
    fun `can change its price`() {
        val tested = Plan.new("plan abc", 400, 1)
        tested.changePrice(500)

        tested.events.shouldEndWith(
            PlanPriceChanged("plan abc", 400, 500)
        )
    }

    @Test
    fun `can be restored from events`() {
        val tested = Plan.new("planId 42", 800, 12)
        tested.changePrice(900)

        val restoredFromEvents = Plan.restoreFrom(AggregateHistory(tested.id, tested.recentEvents()))

        restoredFromEvents.price shouldBe tested.price
        restoredFromEvents.duration shouldBe tested.duration

        tested.recentEvents().shouldBeEmpty()
    }

    @Test
    fun `cannot be restored if no events`() {
        shouldThrow<IllegalArgumentException> {
            Plan.restoreFrom(AggregateHistory(PlanId("planId 42"), listOf()))
        }
    }
}
