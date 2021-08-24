package gym.subscriptions.useCases

import gym.subscriptions.domain.NewSubscription
import gym.subscriptions.domain.SubscriptionDiscountedFor3YearsAnniversary
import gym.subscriptions.domain.SubscriptionRenewed
import gym.subscriptions.infrastructure.InMemorySubscriptionEventStore
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldEndWith

internal class ApplyThreeYearsAnniversaryDiscountTest : AnnotationSpec() {

    @Test
    fun handle() {
        val eventStore = InMemorySubscriptionEventStore()

        val subscriptionId = eventStore.nextId()
        eventStore.storeEvents(
            listOf(
                NewSubscription(subscriptionId, 1000.0, 12, "2015-07-09", "2016-07-09", "leia@gmail.com", false),
                SubscriptionRenewed(subscriptionId, "2016-07-09", "2017-07-09"),
                SubscriptionRenewed(subscriptionId, "2017-07-09", "2018-07-09")
            )
        )

        val tested = ApplyThreeYearsAnniversaryDiscount(eventStore)

        val eventsBeforeThreeYearsAnniversary = tested(
            ApplyThreeYearsAnniversaryDiscountCommand("2018-07-08")
        )
        eventsBeforeThreeYearsAnniversary.shouldBeEmpty()

        val eventsWithThreeYearsDiscount = tested(
            ApplyThreeYearsAnniversaryDiscountCommand("2018-07-09")
        )
        eventsWithThreeYearsDiscount shouldEndWith SubscriptionDiscountedFor3YearsAnniversary(
            subscriptionId,
            950.0
        )

        val eventsAfterThreeYearsAnniversary = tested(
            ApplyThreeYearsAnniversaryDiscountCommand("2018-07-10")
        )
        eventsAfterThreeYearsAnniversary.shouldBeEmpty()
    }
}
