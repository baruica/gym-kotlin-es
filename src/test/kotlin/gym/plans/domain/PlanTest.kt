package gym.plans.domain

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PlanTest {

    @Test
    fun `a duration cannot be anything but 1 month or 12 months`() {
        assertFailsWith<IllegalArgumentException> {
            Plan(PlanId("abc"), 400, 4)
        }
    }

    @Test
    fun `a price cannot be negative`() {
        assertFailsWith<IllegalArgumentException> {
            Plan(PlanId("abc"), -10, 1)
        }
    }

    @Test
    fun `can change its price`() {
        val tested = Plan(PlanId("abc"), 400, 1)
        tested.changePrice(500)

        assertEquals(
            Plan(PlanId("abc"), 500, 1),
            tested
        )
    }

    @Test
    fun `can be restored from events`() {
        val tested = Plan(PlanId("aggregateId"), 800, 12)
        tested.changePrice(900)

        val restoredFromEvents = Plan.restoreFrom(tested.history)

        assertEquals(tested, restoredFromEvents)
    }

    @Test
    fun `cannot be restored if no events`() {
        assertFailsWith<IllegalArgumentException> {
            Plan.restoreFrom(listOf())
        }
    }
}
