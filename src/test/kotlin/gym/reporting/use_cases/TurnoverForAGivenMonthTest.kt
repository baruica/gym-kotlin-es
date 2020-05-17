package gym.reporting.use_cases

import gym.subscriptions.domain.Subscription
import gym.subscriptions.infrastructure.SubscriptionInMemoryRepository
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals

class TurnoverForAGivenMonthTest {

    @Test
    fun `turnover for a given month with ongoing subscriptions`() {
        val subscriptionRepository = SubscriptionInMemoryRepository()

        val today = LocalDate.parse("2018-06-09")
        val inAMonth = LocalDate.parse("2018-07-09")

        subscriptionRepository.store(
            Subscription(
                subscriptionRepository.nextId(),
                50,
                today,
                1,
                "bob@gmail.com",
                false
            )
        )
        subscriptionRepository.store(
            Subscription(
                subscriptionRepository.nextId(),
                500,
                inAMonth,
                12,
                "bob@gmail.com",
                false
            )
        )

        val tested = TurnoverForAGivenMonth(subscriptionRepository)

        assertEquals(50.toDouble(), tested.handle(TurnoverForAGivenMonthQuery(today)))
        assertEquals(1, subscriptionRepository.onGoingSubscriptions(today).size)

        assertEquals(29.toDouble(), tested.handle(TurnoverForAGivenMonthQuery(inAMonth)))
        assertEquals(1, subscriptionRepository.onGoingSubscriptions(inAMonth).size)
    }
}
