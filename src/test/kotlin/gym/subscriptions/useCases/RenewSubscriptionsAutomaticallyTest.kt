package gym.subscriptions.useCases

import gym.subscriptions.domain.NewSubscription
import gym.subscriptions.domain.SubscriptionId
import gym.subscriptions.domain.SubscriptionRenewed
import gym.subscriptions.infrastructure.InMemorySubscriptionEventStore
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RenewSubscriptionsAutomaticallyTest {

    @Test
    fun handle() {
        val subscriptionEventStore = InMemorySubscriptionEventStore()
        val subscriptionId = subscriptionEventStore.nextId()

        subscriptionEventStore.store(
            listOf(
                NewSubscription(
                    subscriptionId,
                    300,
                    1,
                    "2018-06-09",
                    "2018-07-09",
                    "luke@gmail.com",
                    false
                )
            )
        )

        val tested = RenewSubscriptionsAutomatically(subscriptionEventStore)

        tested.handle(
            RenewSubscriptionsAutomaticallyCommand("2018-07-09")
        )

        val aggregateHistory = subscriptionEventStore.getAggregateHistory(SubscriptionId(subscriptionId))

        assertEquals(
            aggregateHistory.events.last(),
            SubscriptionRenewed(
                subscriptionId,
                "2018-07-09",
                "2018-08-08"
            )
        )
    }
}
