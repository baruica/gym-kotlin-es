package gym.reporting.useCases

import gym.reporting.Turnover
import gym.subscriptions.domain.NewSubscription
import gym.subscriptions.domain.SubscriptionRenewed
import gym.subscriptions.infrastructure.InMemorySubscriptionEventStore
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class TurnoverForAGivenMonthTest : AnnotationSpec() {

    @Test
    fun `turnover for a given month with ongoing subscriptions`() {
        val eventStore = InMemorySubscriptionEventStore()

        val today = LocalDate.parse("2018-06-09")
        val inAMonth = LocalDate.parse("2018-07-09")
        val inTwoMonths = LocalDate.parse("2018-08-09")

        val subscriptionToBeRenewedId = eventStore.nextId()
        val subscriptionToBeRenewedEndDate = today.plusMonths(1)

        eventStore.storeEvents(
            listOf(
                NewSubscription(
                    subscriptionToBeRenewedId,
                    50.0,
                    1,
                    today.toString(),
                    subscriptionToBeRenewedEndDate.toString(),
                    "bob@gmail.com",
                    isStudent = false
                ),
                NewSubscription(
                    eventStore.nextId(),
                    400.0,
                    12,
                    today.toString(),
                    today.plusMonths(12).toString(),
                    "bob@gmail.com",
                    isStudent = false
                ),
                SubscriptionRenewed(
                    subscriptionToBeRenewedId,
                    subscriptionToBeRenewedEndDate.toString(),
                    subscriptionToBeRenewedEndDate.plusMonths(1).toString()
                ),
                NewSubscription(
                    eventStore.nextId(),
                    500.0,
                    12,
                    inAMonth.toString(),
                    inAMonth.plusMonths(12).toString(),
                    "bob@gmail.com",
                    isStudent = false
                )
            )
        )

        val tested = TurnoverForAGivenMonthHandler(eventStore)

        eventStore.onGoingSubscriptions(today).shouldHaveSize(2)
        tested(TurnoverForAGivenMonth(today)) shouldBe Turnover(83)

        eventStore.onGoingSubscriptions(inTwoMonths).shouldHaveSize(3)
        tested(TurnoverForAGivenMonth(inTwoMonths)) shouldBe Turnover(125)
    }
}
