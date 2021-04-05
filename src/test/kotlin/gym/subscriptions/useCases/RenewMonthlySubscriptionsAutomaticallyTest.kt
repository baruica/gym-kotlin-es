package gym.subscriptions.useCases

import gym.subscriptions.domain.NewSubscription
import gym.subscriptions.domain.SubscriptionRenewed
import gym.subscriptions.infrastructure.InMemorySubscriptionEventStore
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RenewMonthlySubscriptionsAutomaticallyTest {

    @Test
    fun handle() {
        val subscriptionEventStore = InMemorySubscriptionEventStore()
        val monthlySubscriptionId = subscriptionEventStore.nextId()
        val yearlySubscriptionId = subscriptionEventStore.nextId()

        subscriptionEventStore.store(
            listOf(
                NewSubscription(
                    monthlySubscriptionId,
                    300.0,
                    1,
                    "2018-06-09",
                    "2018-07-09",
                    "luke@gmail.com",
                    false
                ),
                NewSubscription(
                    yearlySubscriptionId,
                    1200.0,
                    12,
                    "2018-06-12",
                    "2019-06-12",
                    "leia@gmail.com",
                    true
                )
            )
        )

        val tested = RenewMonthlySubscriptionsAutomatically(subscriptionEventStore)

        val events = tested.handle(
            RenewMonthlySubscriptionsAutomaticallyCommand("2018-07-09")
        )

        assertEquals(1, events.size)
        assertEquals(
            events.last(),
            SubscriptionRenewed(
                monthlySubscriptionId,
                "2018-07-09",
                "2018-08-08"
            )
        )
    }
}
