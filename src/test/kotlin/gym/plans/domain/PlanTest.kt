package gym.plans.domain

import AggregateHistory
import Id
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldEndWith
import io.kotest.matchers.shouldBe

class PlanTest : StringSpec({

    "a duration cannot be anything but 1 month or 12 months" {
        shouldThrow<IllegalArgumentException> {
            Plan.new(Id("plan abc"), 400, 4)
        }
    }

    "a price cannot be negative" {
        shouldThrow<IllegalArgumentException> {
            Plan.new(Id("plan abc"), -10, 1)
        }
    }

    "can change its price" {
        val (tested, _) = Plan.new(Id("plan abc"), 400, 1)
        val (_, events) = tested.changePrice(500)

        events.shouldEndWith(
            PlanPriceChanged("plan abc", 400, 500)
        )
    }

    "can be restored from events" {
        val (tested, newPlanEvent) = Plan.new(Id("planId 42"), 800, 12)
        val (_, changePriceevent) = tested.changePrice(900)

        val restoredFromEvents = Plan.restoreFrom(AggregateHistory(tested.id, newPlanEvent + changePriceevent))

        restoredFromEvents.price shouldBe tested.price
        restoredFromEvents.duration shouldBe tested.duration
    }

    "cannot be restored if no events" {
        shouldThrow<IllegalArgumentException> {
            Plan.restoreFrom(AggregateHistory(Id("planId 42"), listOf()))
        }
    }
})
