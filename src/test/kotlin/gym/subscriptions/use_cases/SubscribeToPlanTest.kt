package gym.subscriptions.use_cases

import gym.subscriptions.domain.NewSubscription
import gym.subscriptions.infrastructure.SubscriptionInMemoryEventStore
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SubscribeToPlanTest {

    @Test
    fun handle() {
        val subscriptionEventStore = SubscriptionInMemoryEventStore()
        val subscriptionId = subscriptionEventStore.nextId()

        val tested = SubscribeToPlan(subscriptionEventStore)

        tested.handle(
            SubscribeToPlanCommand(
                subscriptionId.toString(),
                1000,
                12,
                "2018-12-18",
                false,
                "bob@mail.com"
            )
        )

        val events = subscriptionEventStore.getAllEvents(subscriptionId)

        assertEquals(1, events.size)
        assertEquals(
            events.last(),
            NewSubscription(
                events.last().subscriptionId,
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
