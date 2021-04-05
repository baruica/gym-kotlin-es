package gym.subscriptions.useCases

import gym.subscriptions.domain.NewSubscription
import gym.subscriptions.domain.SubscriptionId
import gym.subscriptions.infrastructure.InMemorySubscriptionEventStore
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SubscribeToPlanTest {

    @Test
    fun handle() {
        val eventStore = InMemorySubscriptionEventStore()
        val subscriptionId = eventStore.nextId()

        val tested = SubscribeToPlan(eventStore)

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

        val aggregateHistory = eventStore.getAggregateHistory(SubscriptionId(subscriptionId))

        assertEquals(1, aggregateHistory.events.size)
        assertEquals(
            NewSubscription(
                aggregateHistory.aggregateId.toString(),
                900.0,
                12,
                "2018-12-18",
                "2019-12-18",
                "bob@mail.com",
                false
            ),
            aggregateHistory.events.last()
        )
    }
}
