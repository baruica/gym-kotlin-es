package gym.subscriptions.useCases

import Id
import gym.membership.domain.EmailAddress
import gym.subscriptions.domain.NewSubscription
import gym.subscriptions.infrastructure.InMemorySubscriptionEventStore
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class SubscribeToPlanTest : AnnotationSpec() {

    @Test
    fun handle() {
        val eventStore = InMemorySubscriptionEventStore()
        val subscriptionId = eventStore.nextId()

        val tested = SubscribeToPlan.Handler(eventStore)

        val events = tested(
            SubscribeToPlan(
                Id(subscriptionId),
                1000,
                12,
                LocalDate.parse("2018-12-18"),
                false,
                EmailAddress("bob@mail.com")
            )
        )

        events.last().shouldBe(
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
