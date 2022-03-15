package gym.subscriptions.useCases

import gym.subscriptions.domain.NewSubscription
import gym.subscriptions.domain.SubscriptionRenewed
import gym.subscriptions.infrastructure.InMemorySubscriptionEventStore
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.collections.shouldEndWith
import io.kotest.matchers.collections.shouldHaveSize
import java.time.LocalDate

class RenewMonthlySubscriptionsAutomaticallyTest : AnnotationSpec() {

    @Test
    fun handle() {
        val eventStore = InMemorySubscriptionEventStore()
        val monthlySubscriptionId = eventStore.nextId()
        val yearlySubscriptionId = eventStore.nextId()

        eventStore.storeEvents(
            listOf(
                NewSubscription(
                    monthlySubscriptionId,
                    300.0,
                    1,
                    "2018-06-09",
                    "2018-07-09",
                    "luke@gmail.com",
                    isStudent = false
                ),
                NewSubscription(
                    yearlySubscriptionId,
                    1200.0,
                    12,
                    "2018-06-12",
                    "2019-06-12",
                    "leia@gmail.com",
                    isStudent = true
                )
            )
        )

        val tested = RenewMonthlySubscriptionsAutomatically.Handler(eventStore)

        val events = tested(
            RenewMonthlySubscriptionsAutomatically(LocalDate.parse("2018-07-09"))
        )

        events.shouldHaveSize(1)
        events.shouldEndWith(
            SubscriptionRenewed(
                monthlySubscriptionId,
                "2018-07-09",
                "2018-08-09"
            )
        )
    }
}
