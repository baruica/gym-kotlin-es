package gym.subscriptions.use_cases

import gym.subscriptions.domain.SubscriptionEvent.NewSubscription
import gym.subscriptions.domain.SubscriptionEvent.SubscriptionRenewed
import gym.subscriptions.infrastructure.SubscriptionInMemoryEventStore
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RenewSubscriptionsAutomaticallyTest {

    @Test
    fun handle() {
        val subscriptionEventStore = SubscriptionInMemoryEventStore()
        val subscriptionId = subscriptionEventStore.nextId()

        subscriptionEventStore.store(listOf(
            NewSubscription(
                subscriptionId.toString(),
                300,
                1,
                "2018-06-09",
                "2018-07-09",
                "luke@gmail.com",
                false
            )
        ))

        val tested = RenewSubscriptionsAutomatically(subscriptionEventStore)

        tested.handle(
            RenewSubscriptionsAutomaticallyCommand("2018-07-09")
        )

        val events = subscriptionEventStore.getAllEvents(subscriptionId)

        assertEquals(
            events.last(),
            SubscriptionRenewed(
                subscriptionId.toString(),
                "2018-07-09",
                "2018-08-08"
            )
        )
    }
}
