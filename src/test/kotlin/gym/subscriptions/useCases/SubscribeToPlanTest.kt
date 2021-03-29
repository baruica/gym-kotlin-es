package gym.subscriptions.useCases

import gym.subscriptions.domain.NewSubscription
import gym.subscriptions.domain.SubscriptionId
import gym.subscriptions.infrastructure.InMemorySubscriptionEventStore
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SubscribeToPlanTest {

    @Test
    fun handle() {
        val subscriptionEventStore = InMemorySubscriptionEventStore()
        val subscriptionId = subscriptionEventStore.nextId()

        val tested = SubscribeToPlan(subscriptionEventStore)

        tested.handle(
            SubscribeToPlanCommand(
                subscriptionId,
                1000,
                12,
                "2018-12-18",
                false,
                "bob@mail.com"
            )
        )

        val aggregateHistory = subscriptionEventStore.getAggregateHistory(SubscriptionId(subscriptionId))

        assertEquals(1, aggregateHistory.events.size)
        assertEquals(
            aggregateHistory.events.last(),
            NewSubscription(
                aggregateHistory.aggregateId.toString(),
                700,
                12,
                "2018-12-18",
                "2019-12-17",
                "bob@mail.com",
                false
            )
        )
    }
}
