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

        assertEquals(300, (subscriptionWithoutDiscount.recordedEvents.last() as NewSubscription).subscriptionPrice)
    }

    @Test
    fun `30% discount for yearly subscription`() {
        val subscriptionWithYearlyDiscount = yearlySubscription(1000, fifthOfJune(), false)

        assertEquals(700, (subscriptionWithYearlyDiscount.recordedEvents.last() as NewSubscription).subscriptionPrice)
    }

    @Test
    fun `20% discount for students`() {
        val monthlySubscriptionWithStudentDiscount = monthlySubscription(100, fifthOfJune(), true)
        assertEquals(80, (monthlySubscriptionWithStudentDiscount.recordedEvents.last() as NewSubscription).subscriptionPrice)

        val yearlySubscriptionWithStudentDiscount = yearlySubscription(100, fifthOfJune(), true)
        assertEquals(50, (yearlySubscriptionWithStudentDiscount.recordedEvents.last() as NewSubscription).subscriptionPrice)
    }

    @Test
    fun `can be renewed`() {
        val subscription = monthlySubscription(100, fifthOfJune(), isStudent = false)
        assertEquals("2018-07-04", (subscription.recordedEvents.last()).getEndDate())

        subscription.renew()
        assertEquals("2018-08-03", (subscription.recordedEvents.last()).getEndDate())
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
        assertEquals(100.0, monthlySubscription.monthlyTurnover())

        val yearlySubscription = yearlySubscription(1200, fifthOfJune())
        assertEquals(70.0, yearlySubscription.monthlyTurnover())
    }

    @Test
    fun `can tell if it'll be ended as from a given date`() {
        val subscriptionEndingEndOfJune = monthlySubscription(100, fifthOfJune())

        //assertFalse(subscriptionEndingEndOfJune.willBeEnded(LocalDate.parse("2018-07-04")))
        //assertTrue(subscriptionEndingEndOfJune.willBeEnded(LocalDate.parse("2018-07-05")))
    }

    @Test
    fun `can be restored from events`() {
        val subscriptionId = SubscriptionId("aggregateId")
        val tested = Subscription(
            subscriptionId,
            LocalDate.parse("2018-07-04"),
            12,
            900,
            "Han@gmail.com",
            isStudent = false
        )
        tested.renew()

        val restoredFromEvents = Subscription.restoreFrom(AggregateHistory(subscriptionId, tested.recordedEvents))

        assertEquals(tested, restoredFromEvents)
    }

    @Test
    fun `cannot be restored if no events`() {
        assertFailsWith<IllegalArgumentException> {
            Subscription.restoreFrom(AggregateHistory(SubscriptionId("subscriptionId 42"), listOf()))
        }
    }

    private fun monthlySubscription(basePrice: Int, startDate: LocalDate, isStudent: Boolean = false): Subscription {
        return newSubscription(startDate, basePrice, 1, isStudent)
    }

    private fun yearlySubscription(basePrice: Int, startDate: LocalDate, isStudent: Boolean = false): Subscription {
        return newSubscription(startDate, basePrice, 12, isStudent)
    }

    private fun newSubscription(
        startDate: LocalDate,
        basePrice: Int,
        durationInMonths: Int,
        isStudent: Boolean
    ): Subscription {
        return Subscription(
            SubscriptionId(UUID.randomUUID().toString()),
            startDate,
            durationInMonths,
            basePrice,
            "luke@gmail.com",
            isStudent
        )
    }
}
