package gym.subscriptions.useCases

import gym.subscriptions.domain.NewSubscription
import gym.subscriptions.domain.SubscriptionDiscountedFor3YearsAnniversary
import gym.subscriptions.domain.SubscriptionRenewed
import gym.subscriptions.infrastructure.InMemorySubscriptionEventStore
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldEndWith
import java.time.LocalDate

internal class ApplyThreeYearsAnniversaryDiscountTest : StringSpec({

    "handle" {
        val eventStore = InMemorySubscriptionEventStore()

        val subscriptionId = eventStore.nextId()
        eventStore.storeEvents(
            listOf(
                NewSubscription(
                    subscriptionId,
                    1000.0,
                    12,
                    "2015-07-09",
                    "2016-07-09",
                    "leia@gmail.com",
                    isStudent = false
                ),
                SubscriptionRenewed(subscriptionId, "2016-07-09", "2017-07-09"),
                SubscriptionRenewed(subscriptionId, "2017-07-09", "2018-07-09")
            )
        )

        val tested = ApplyThreeYearsAnniversaryDiscount.Handler(eventStore)

        val eventsBeforeThreeYearsAnniversary = tested(
            ApplyThreeYearsAnniversaryDiscount(LocalDate.parse("2018-07-08"))
        )
        eventsBeforeThreeYearsAnniversary.shouldBeEmpty()

        val eventsWithThreeYearsDiscount = tested(
            ApplyThreeYearsAnniversaryDiscount(LocalDate.parse("2018-07-09"))
        )
        eventsWithThreeYearsDiscount shouldEndWith SubscriptionDiscountedFor3YearsAnniversary(
            subscriptionId,
            950.0
        )

        val eventsAfterThreeYearsAnniversary = tested(
            ApplyThreeYearsAnniversaryDiscount(LocalDate.parse("2018-07-10"))
        )
        eventsAfterThreeYearsAnniversary.shouldBeEmpty()
    }
})
