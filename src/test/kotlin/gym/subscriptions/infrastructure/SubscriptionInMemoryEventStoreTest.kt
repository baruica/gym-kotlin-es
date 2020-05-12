package gym.subscriptions.infrastructure

import gym.subscriptions.domain.SubscriptionEvent.NewSubscription
import gym.subscriptions.domain.SubscriptionEvent.SubscriptionRenewed
import gym.subscriptions.domain.SubscriptionId
import org.junit.Test
import kotlin.test.assertEquals

class SubscriptionInMemoryEventStoreTest {

    @Test
    fun `what is stores can be retrived`() {
        val tested = SubscriptionInMemoryEventStore()

        val subscriptionId1Event1 = NewSubscription("subscriptionId1", 300, 12, "2018-02-23", "2019-02-23", "Luke@gmail.com", false)
        val subscriptionId1Event2 = SubscriptionRenewed("subscriptionId1", "2019-02-23", "2020-02-23")
        val subscriptionId2Event1 = NewSubscription("subscriptionId2", 90, 1, "2018-10-23", "2018-11-23", "Han@gmail.com", false)
        val subscriptionId2Event2 = SubscriptionRenewed("subscriptionId2", "2018-11-23", "2018-12-23")

        tested.store(listOf(
            subscriptionId1Event1,
            subscriptionId1Event2,
            subscriptionId2Event1,
            subscriptionId2Event2
        ))

        assertEquals(
            listOf(
                subscriptionId1Event1,
                subscriptionId1Event2
            ),
            tested.getAllEvents(SubscriptionId("subscriptionId1"))
        )

        assertEquals(
            listOf(
                subscriptionId2Event1,
                subscriptionId2Event2
            ),
            tested.getAllEvents(SubscriptionId("subscriptionId2"))
        )
    }
}
