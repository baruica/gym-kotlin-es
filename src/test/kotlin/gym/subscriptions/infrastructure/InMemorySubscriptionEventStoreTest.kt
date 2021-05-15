package gym.subscriptions.infrastructure

import gym.subscriptions.domain.NewSubscription
import gym.subscriptions.domain.SubscriptionId
import gym.subscriptions.domain.SubscriptionRenewed
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.collections.shouldContainExactly

class InMemorySubscriptionEventStoreTest : AnnotationSpec() {

    @Test
    fun `what is stores can be retrived`() {
        val tested = InMemorySubscriptionEventStore()

        val subscriptionId1Event1 =
            NewSubscription("subscriptionId1", 300.0, 12, "2018-02-23", "2019-02-23", "Luke@gmail.com", false)
        val subscriptionId1Event2 = SubscriptionRenewed("subscriptionId1", "2019-02-23", "2020-02-23")

        val subscriptionId2Event1 =
            NewSubscription("subscriptionId2", 90.0, 1, "2018-10-23", "2018-11-23", "Han@gmail.com", false)
        val subscriptionId2Event2 = SubscriptionRenewed("subscriptionId2", "2018-11-23", "2018-12-23")

        tested.store(
            listOf(
                subscriptionId1Event1,
                subscriptionId1Event2,
                subscriptionId2Event1,
                subscriptionId2Event2
            )
        )

        tested.getAggregateHistory(SubscriptionId("subscriptionId1")).events.shouldContainExactly(
            listOf(
                subscriptionId1Event1,
                subscriptionId1Event2
            )
        )

        tested.getAggregateHistory(SubscriptionId("subscriptionId2")).events.shouldContainExactly(
            listOf(
                subscriptionId2Event1,
                subscriptionId2Event2
            )
        )
    }
}
