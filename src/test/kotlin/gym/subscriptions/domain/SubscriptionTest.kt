package gym.subscriptions.domain

import common.AggregateHistory
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SubscriptionTest {

    @Test
    fun `no discount for a non-student subscribing to a monthly subscription`() {
        val subscriptionWithoutDiscount = monthlySubscription(300, LocalDate.parse("2018-06-05"), false)

        assertEquals(300.0, (subscriptionWithoutDiscount.occuredEvents().last() as NewSubscription).subscriptionPrice)
    }

    @Test
    fun `10 percent discount for yearly subscription`() {
        val subscriptionWithYearlyDiscount = yearlySubscription(1000, LocalDate.parse("2018-06-05"), false)

        assertEquals(
            900.0,
            (subscriptionWithYearlyDiscount.occuredEvents().last() as NewSubscription).subscriptionPrice
        )
    }

    @Test
    fun `20 percent discount for students`() {
        val monthlySubscriptionWithStudentDiscount = monthlySubscription(100, LocalDate.parse("2018-06-05"), true)
        assertEquals(
            80.0,
            (monthlySubscriptionWithStudentDiscount.occuredEvents().last() as NewSubscription).subscriptionPrice
        )

        val yearlySubscriptionWithStudentDiscount = yearlySubscription(100, LocalDate.parse("2018-06-05"), true)
        assertEquals(
            72.0,
            (yearlySubscriptionWithStudentDiscount.occuredEvents().last() as NewSubscription).subscriptionPrice
        )
    }

    @Test
    fun `5 percent discount after 3 years`() {
        val subscription = yearlySubscription(1000, LocalDate.parse("2018-06-05"))
        assertEquals(Price(900), subscription.price)

        subscription.renew()
        assertEquals(Price(900), subscription.price)

        subscription.renew()
        subscription.applyThreeYearsAnniversaryDiscount(LocalDate.parse("2021-06-05"))
        assertEquals(Price(855), subscription.price)
    }

    @Test
    fun `3 years anniversary discount can only be applied once`() {
        val subscription = yearlySubscription(1000, LocalDate.parse("2018-06-05"))

        subscription.applyThreeYearsAnniversaryDiscount(LocalDate.parse("2021-06-05"))
        assertEquals(Price(900), subscription.price)

        subscription.renew()
        subscription.renew()
        subscription.applyThreeYearsAnniversaryDiscount(LocalDate.parse("2021-06-05"))
        assertEquals(Price(855), subscription.price)

        subscription.applyThreeYearsAnniversaryDiscount(LocalDate.parse("2021-06-05"))
        assertEquals(Price(855), subscription.price)
    }

    @Test
    fun `can be renewed`() {
        val subscription = yearlySubscription(1000, LocalDate.parse("2018-06-05"), isStudent = false)
        assertEquals("2019-06-05", ((subscription.occuredEvents().last()) as NewSubscription).subscriptionEndDate)

        subscription.renew()
        assertEquals("2020-06-05", ((subscription.occuredEvents().last()) as SubscriptionRenewed).newEndDate)
    }

    @Test
    fun `can be ongoing`() {
        val ongoingSubscription = monthlySubscription(100, LocalDate.parse("2018-06-05"))

        assertFalse(ongoingSubscription.isOngoing(LocalDate.parse("2018-06-04")))
        assertTrue(ongoingSubscription.isOngoing(LocalDate.parse("2018-06-05")))
        assertTrue(ongoingSubscription.isOngoing(LocalDate.parse("2018-06-19")))
        assertTrue(ongoingSubscription.isOngoing(LocalDate.parse("2018-07-05")))
        assertFalse(ongoingSubscription.isOngoing(LocalDate.parse("2018-07-06")))
    }

    @Test
    fun `has a monthly turnover`() {
        val monthlySubscription = monthlySubscription(100, LocalDate.parse("2018-06-05"))
        assertEquals(100, monthlySubscription.monthlyTurnover())

        val yearlySubscription = yearlySubscription(1200, LocalDate.parse("2018-06-05"))
        assertEquals(90, yearlySubscription.monthlyTurnover())
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
