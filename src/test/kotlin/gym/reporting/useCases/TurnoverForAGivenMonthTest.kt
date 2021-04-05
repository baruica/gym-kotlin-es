package gym.reporting.useCases

import gym.reporting.Turnover
import gym.subscriptions.domain.NewSubscription
import gym.subscriptions.domain.SubscriptionRenewed
import gym.subscriptions.infrastructure.InMemorySubscriptionEventStore
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals

class TurnoverForAGivenMonthTest {

    @Test
    fun `turnover for a given month with ongoing subscriptions`() {
        val subscriptionEventStore = InMemorySubscriptionEventStore()

        val today = LocalDate.parse("2018-06-09")
        val inAMonth = LocalDate.parse("2018-07-09")
        val inTwoMonths = LocalDate.parse("2018-08-09")

        val subscriptionToBeRenewedId = subscriptionEventStore.nextId()
        val subscriptionToBeRenewedEndDate = today.plusMonths(1)

        subscriptionEventStore.store(
            listOf(
                NewSubscription(
                    subscriptionToBeRenewedId,
                    50.0,
                    1,
                    today.toString(),
                    subscriptionToBeRenewedEndDate.toString(),
                    "bob@gmail.com",
                    false
                ),
                NewSubscription(
                    subscriptionEventStore.nextId(),
                    400.0,
                    12,
                    today.toString(),
                    today.plusMonths(12).toString(),
                    "bob@gmail.com",
                    false
                ),
                SubscriptionRenewed(
                    subscriptionToBeRenewedId,
                    subscriptionToBeRenewedEndDate.toString(),
                    subscriptionToBeRenewedEndDate.plusMonths(1).toString()
                ),
                NewSubscription(
                    subscriptionEventStore.nextId(),
                    500.0,
                    12,
                    inAMonth.toString(),
                    inAMonth.plusMonths(12).toString(),
                    "bob@gmail.com",
                    false
                )
            )
        )

        val tested = TurnoverForAGivenMonth(subscriptionEventStore)

        assertEquals(2, subscriptionEventStore.onGoingSubscriptions(today).size)
        assertEquals(Turnover(83), tested.handle(TurnoverForAGivenMonthQuery(today)))

        assertEquals(3, subscriptionEventStore.onGoingSubscriptions(inTwoMonths).size)
        assertEquals(Turnover(125), tested.handle(TurnoverForAGivenMonthQuery(inTwoMonths)))
    }
}
