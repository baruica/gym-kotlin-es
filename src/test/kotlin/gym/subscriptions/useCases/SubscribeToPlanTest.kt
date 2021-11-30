package gym.subscriptions.useCases

import gym.subscriptions.domain.NewSubscription
import gym.subscriptions.infrastructure.InMemorySubscriptionEventStore
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.types.shouldBeSameInstanceAs

class SubscribeToPlanTest : AnnotationSpec() {

    @Test
    fun handle() {
        val eventStore = InMemorySubscriptionEventStore()
        val subscriptionId = eventStore.nextId()

        val tested = SubscribeToPlan(eventStore)

        val events = tested(
            SubscribeToPlanCommand(
                subscriptionId,
                1000,
                12,
                "2018-12-18",
                false,
                "bob@mail.com"
            )
        )

        events.last().shouldBeSameInstanceAs(
            NewSubscription(
                subscriptionId,
                900.0,
                12,
                "2018-12-18",
                "2019-12-18",
                "bob@mail.com",
                isStudent = false
            )
        )
    }
}
