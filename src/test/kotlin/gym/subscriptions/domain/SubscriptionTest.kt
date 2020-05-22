package gym.subscriptions.domain

import gym.fifthOfJune
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SubscriptionTest {

    @Test
    fun `no discount for monthly subscription`() {
        val subscriptionWithoutDiscount = monthlySubscription(300, fifthOfJune(), false)

        assertEquals(300, (subscriptionWithoutDiscount.history.last() as NewSubscription).subscriptionPrice)
    }

    @Test
    fun `30% discount for yearly subscription`() {
        val subscriptionWithYearlyDiscount = yearlySubscription(1000, fifthOfJune(), false)

        assertEquals(700, (subscriptionWithYearlyDiscount.history.last() as NewSubscription).subscriptionPrice)
    }

    @Test
    fun `20% discount for students`() {
        val monthlySubscriptionWithStudentDiscount = monthlySubscription(100, fifthOfJune(), true)
        assertEquals(80, (monthlySubscriptionWithStudentDiscount.history.last() as NewSubscription).subscriptionPrice)

        val yearlySubscriptionWithStudentDiscount = yearlySubscription(100, fifthOfJune(), true)
        assertEquals(50, (yearlySubscriptionWithStudentDiscount.history.last() as NewSubscription).subscriptionPrice)
    }

    @Test
    fun `can be renewed`() {
        val subscription = monthlySubscription(100, fifthOfJune(), isStudent = false)
        assertEquals("2018-07-04", (subscription.history.last()).getEndDate())

        subscription.renew()
        assertEquals("2018-08-03", (subscription.history.last()).getEndDate())
    }

    @Test
    fun `can be ongoing`() {
        val ongoingSubscription = monthlySubscription(100, fifthOfJune(), isStudent = false)

        val dateInJune = LocalDate.parse("2018-06-19")

        //assertTrue(ongoingSubscription.isOngoing(dateInJune))
    }

    @Test
    fun `has a monthly turnover`() {
        val monthlySubscription = monthlySubscription(100, fifthOfJune(), isStudent = false)
        assertEquals(100.0, monthlySubscription.monthlyTurnover())

        val yearlySubscription = yearlySubscription(1200, fifthOfJune(), isStudent = false)
        assertEquals(70.0, yearlySubscription.monthlyTurnover())
    }

    @Test
    fun `can tell if it'll be ended as from a given date`() {
        val subscriptionEndingEndOfJune = monthlySubscription(100, fifthOfJune(), isStudent = false)

        //assertFalse(subscriptionEndingEndOfJune.willBeEnded(LocalDate.parse("2018-07-04")))
        //assertTrue(subscriptionEndingEndOfJune.willBeEnded(LocalDate.parse("2018-07-05")))
    }

    @Test
    fun `can be restored from events`() {
        val tested = Subscription(
            SubscriptionId("aggregateId"),
            LocalDate.parse("2018-07-04"),
            12,
            900,
            "Han@gmail.com",
            isStudent = false
        )
        tested.renew()

        val restoredFromEvents = Subscription.restoreFrom(tested.history)

        assertEquals(tested, restoredFromEvents)
    }

    @Test
    fun `cannot be restored if no events`() {
        assertFailsWith<IllegalArgumentException> {
            Subscription.restoreFrom(listOf())
        }
    }

    private fun monthlySubscription(basePrice: Int, startDate: LocalDate, isStudent: Boolean): Subscription {
        return newSubscription(startDate, basePrice, 1, isStudent)
    }

    private fun yearlySubscription(basePrice: Int, startDate: LocalDate, isStudent: Boolean): Subscription {
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
