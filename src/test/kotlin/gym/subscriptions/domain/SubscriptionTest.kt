package gym.subscriptions.domain

import AggregateHistory
import AggregateResult
import Id
import com.github.guepardoapps.kulid.ULID
import gym.membership.domain.EmailAddress
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class SubscriptionTest : AnnotationSpec() {

    @Test
    fun `no discount for a non-student subscribing to a monthly subscription`() {
        val subscriptionWithoutDiscount = monthlySubscription(300, LocalDate.parse("2018-06-05"), false)

        (subscriptionWithoutDiscount.events.last() as NewSubscription).subscriptionPrice shouldBe 300
    }

    @Test
    fun `10 percent discount for yearly subscription`() {
        val subscriptionWithYearlyDiscount = yearlySubscription(1000, LocalDate.parse("2018-06-05"), false)

        (subscriptionWithYearlyDiscount.events.last() as NewSubscription).subscriptionPrice shouldBe 900
    }

    @Test
    fun `20 percent discount for students`() {
        val monthlySubscriptionWithStudentDiscount = monthlySubscription(100, LocalDate.parse("2018-06-05"), true)
        (monthlySubscriptionWithStudentDiscount.events.last() as NewSubscription).subscriptionPrice shouldBe 80

        val yearlySubscriptionWithStudentDiscount = yearlySubscription(100, LocalDate.parse("2018-06-05"), true)
        (yearlySubscriptionWithStudentDiscount.events.last() as NewSubscription).subscriptionPrice shouldBe 72
    }

    @Test
    fun `5 percent discount after 3 years`() {
        val (subscription, _) = yearlySubscription(1000, LocalDate.parse("2018-06-05"))
        subscription.price shouldBe Price(900)

        subscription.renew()
        subscription.price shouldBe Price(900)

        subscription.renew()
        subscription.applyThreeYearsAnniversaryDiscount(LocalDate.parse("2021-06-05"))
        subscription.price shouldBe Price(855)
    }

    @Test
    fun `3 years anniversary discount can only be applied once`() {
        val (subscription, _) = yearlySubscription(1000, LocalDate.parse("2018-06-05"))

        subscription.applyThreeYearsAnniversaryDiscount(LocalDate.parse("2021-06-05"))
        subscription.price shouldBe Price(900)

        subscription.renew()
        subscription.renew()
        subscription.applyThreeYearsAnniversaryDiscount(LocalDate.parse("2021-06-05"))
        subscription.price shouldBe Price(855)

        subscription.applyThreeYearsAnniversaryDiscount(LocalDate.parse("2021-06-05"))
        subscription.price shouldBe Price(855)
    }

    @Test
    fun `can be renewed`() {
        val (subscription, events) = yearlySubscription(1000, LocalDate.parse("2018-06-05"), isStudent = false)
        (events.last() as NewSubscription).subscriptionEndDate shouldBe "2019-06-05"

        val (_, renewedSubscriptionEvents) = subscription.renew()
        renewedSubscriptionEvents.last().newEndDate shouldBe "2020-06-05"
    }

    @Test
    fun `can be ongoing`() {
        val (ongoingSubscription, _) = monthlySubscription(100, LocalDate.parse("2018-06-05"))

        ongoingSubscription.isOngoing(LocalDate.parse("2018-06-04")).shouldBeFalse()
        ongoingSubscription.isOngoing(LocalDate.parse("2018-06-05")).shouldBeTrue()
        ongoingSubscription.isOngoing(LocalDate.parse("2018-06-19")).shouldBeTrue()
        ongoingSubscription.isOngoing(LocalDate.parse("2018-07-05")).shouldBeTrue()
        ongoingSubscription.isOngoing(LocalDate.parse("2018-07-06")).shouldBeFalse()
    }

    @Test
    fun `has a monthly turnover`() {
        val (monthlySubscription, _) = monthlySubscription(100, LocalDate.parse("2018-06-05"))
        monthlySubscription.monthlyTurnover() shouldBe 100

        val (yearlySubscription, _) = yearlySubscription(1200, LocalDate.parse("2018-06-05"))
        yearlySubscription.monthlyTurnover() shouldBe 90
    }

    @Test
    fun `can be restored from events`() {
        val (tested, subscribeEvent) = Subscription.subscribe(
            Id("aggregateId"),
            12,
            LocalDate.parse("2018-07-04"),
            900,
            EmailAddress("Han@gmail.com"),
            isStudent = false
        )
        val (_, renewEvent) = tested.renew()

        val restoredFromEvents = Subscription.restoreFrom(AggregateHistory(tested.id, subscribeEvent + renewEvent))

        restoredFromEvents.price shouldBe tested.price
        restoredFromEvents.startDate shouldBe tested.startDate
        restoredFromEvents.endDate shouldBe tested.endDate
        restoredFromEvents.duration shouldBe tested.duration
    }

    @Test
    fun `cannot be restored if no events`() {
        shouldThrow<IllegalArgumentException> {
            Subscription.restoreFrom(AggregateHistory(Id("subscriptionId 42"), listOf()))
        }
    }

    private fun monthlySubscription(
        basePrice: Int,
        subscriptionDate: LocalDate,
        isStudent: Boolean = false
    ): AggregateResult<String, Subscription, SubscriptionEvent> {
        return newSubscription(subscriptionDate, basePrice, 1, isStudent)
    }

    private fun yearlySubscription(
        basePrice: Int,
        subscriptionDate: LocalDate,
        isStudent: Boolean = false
    ): AggregateResult<String, Subscription, SubscriptionEvent> {
        return newSubscription(subscriptionDate, basePrice, 12, isStudent)
    }

    private fun newSubscription(
        subscriptionDate: LocalDate,
        basePrice: Int,
        durationInMonths: Int,
        isStudent: Boolean
    ): AggregateResult<String, Subscription, SubscriptionEvent> {
        return Subscription.subscribe(
            Id(ULID.random()),
            durationInMonths,
            subscriptionDate,
            basePrice,
            EmailAddress("luke@gmail.com"),
            isStudent
        )
    }
}
