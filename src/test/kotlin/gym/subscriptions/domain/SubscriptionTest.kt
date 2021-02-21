package gym.subscriptions.domain

import common.AggregateHistory
import gym.fifthOfJune
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SubscriptionTest {

    @Test
    fun `no discount for monthly subscription`() {
        val subscriptionWithoutDiscount = monthlySubscription(300, fifthOfJune(), false)

        assertEquals(300, (subscriptionWithoutDiscount.occuredEvents().last() as NewSubscription).subscriptionPrice)
    }

    @Test
    fun `30 percent discount for yearly subscription`() {
        val subscriptionWithYearlyDiscount = yearlySubscription(1000, fifthOfJune(), false)

        assertEquals(700, (subscriptionWithYearlyDiscount.occuredEvents().last() as NewSubscription).subscriptionPrice)
    }

    @Test
    fun `20 percent discount for students`() {
        val monthlySubscriptionWithStudentDiscount = monthlySubscription(100, fifthOfJune(), true)
        assertEquals(
            80,
            (monthlySubscriptionWithStudentDiscount.occuredEvents().last() as NewSubscription).subscriptionPrice
        )

        val yearlySubscriptionWithStudentDiscount = yearlySubscription(100, fifthOfJune(), true)
        assertEquals(
            50,
            (yearlySubscriptionWithStudentDiscount.occuredEvents().last() as NewSubscription).subscriptionPrice
        )
    }

    @Test
    fun `can be renewed`() {
        val subscription = monthlySubscription(100, fifthOfJune(), isStudent = false)
        assertEquals("2018-07-04", ((subscription.occuredEvents().last()) as NewSubscription).subscriptionEndDate)

        subscription.renew()
        assertEquals("2018-08-03", ((subscription.occuredEvents().last()) as SubscriptionRenewed).newEndDate)
    }

    @Test
    fun `can be ongoing`() {
        val ongoingSubscription = monthlySubscription(100, fifthOfJune())

        assertFalse(ongoingSubscription.isOngoing(LocalDate.parse("2018-06-04")))
        assertTrue(ongoingSubscription.isOngoing(LocalDate.parse("2018-06-05")))
        assertTrue(ongoingSubscription.isOngoing(LocalDate.parse("2018-06-19")))
        assertTrue(ongoingSubscription.isOngoing(LocalDate.parse("2018-07-04")))
        assertFalse(ongoingSubscription.isOngoing(LocalDate.parse("2018-07-05")))
    }

    @Test
    fun `has a monthly turnover`() {
        val monthlySubscription = monthlySubscription(100, fifthOfJune())
        assertEquals(100, monthlySubscription.monthlyTurnover())

        val yearlySubscription = yearlySubscription(1200, fifthOfJune())
        assertEquals(70, yearlySubscription.monthlyTurnover())
    }

    @Test
    fun `can be restored from events`() {
        val tested = Subscription.subscribe(
            "aggregateId",
            12,
            LocalDate.parse("2018-07-04"),
            900,
            "Han@gmail.com",
            isStudent = false
        )
        tested.renew()

        val restoredFromEvents = Subscription.restoreFrom(AggregateHistory(tested.id, tested.occuredEvents()))

        assertEquals(tested.price, restoredFromEvents.price)
        assertEquals(tested.startDate, restoredFromEvents.startDate)
        assertEquals(tested.endDate, restoredFromEvents.endDate)
        assertEquals(tested.duration, restoredFromEvents.duration)

        assertEquals(emptyList(), tested.occuredEvents())
    }

    @Test
    fun `cannot be restored if no events`() {
        assertFailsWith<IllegalArgumentException> {
            Subscription.restoreFrom(AggregateHistory(SubscriptionId("subscriptionId 42"), listOf()))
        }
    }

    private fun monthlySubscription(
        basePrice: Int,
        subscriptionDate: LocalDate,
        isStudent: Boolean = false
    ): Subscription {
        return newSubscription(subscriptionDate, basePrice, 1, isStudent)
    }

    private fun yearlySubscription(
        basePrice: Int,
        subscriptionDate: LocalDate,
        isStudent: Boolean = false
    ): Subscription {
        return newSubscription(subscriptionDate, basePrice, 12, isStudent)
    }

    private fun newSubscription(
        subscriptionDate: LocalDate,
        basePrice: Int,
        durationInMonths: Int,
        isStudent: Boolean
    ): Subscription {
        return Subscription.subscribe(
            UUID.randomUUID().toString(),
            durationInMonths,
            subscriptionDate,
            basePrice,
            "luke@gmail.com",
            isStudent
        )
    }
}
