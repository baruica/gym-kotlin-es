package gym.subscriptions.useCases

import gym.subscriptions.domain.NewSubscription
import gym.subscriptions.domain.SubscriptionDiscountedFor3YearsAnniversary
import gym.subscriptions.domain.SubscriptionRenewed
import gym.subscriptions.infrastructure.InMemorySubscriptionEventStore
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class ApplyThreeYearsAnniversaryDiscountTest {

    @Test
    fun handle() {
        val eventStore = InMemorySubscriptionEventStore()

        val subscriptionId = eventStore.nextId()
        eventStore.store(
            listOf(
                NewSubscription(subscriptionId, 1000.0, 12, "2015-07-09", "2016-07-09", "leia@gmail.com", false),
                SubscriptionRenewed(subscriptionId, "2016-07-09", "2017-07-09"),
                SubscriptionRenewed(subscriptionId, "2017-07-09", "2018-07-09")
            )
        )

        val tested = ApplyThreeYearsAnniversaryDiscount(eventStore)

        val eventsBeforeThreeYearsAnniversary = tested.handle(
            ApplyThreeYearsAnniversaryDiscountCommand("2018-07-08")
        )
        assertEquals(0, eventsBeforeThreeYearsAnniversary.size)

        val eventsWithThreeYearsDiscount = tested.handle(
            ApplyThreeYearsAnniversaryDiscountCommand("2018-07-09")
        )
        assertEquals(
            SubscriptionDiscountedFor3YearsAnniversary(
                subscriptionId,
                950.0
            ),
            eventsWithThreeYearsDiscount.last()
        )

        val eventsAfterThreeYearsAnniversary = tested.handle(
            ApplyThreeYearsAnniversaryDiscountCommand("2018-07-10")
        )
        assertEquals(0, eventsAfterThreeYearsAnniversary.size)
    }
}
