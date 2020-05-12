package gym.subscriptions.use_cases

import gym.subscriptions.domain.SubscriptionEvent
import gym.subscriptions.domain.SubscriptionEventStore
import java.time.LocalDate

class RenewSubscriptionsAutomatically(
    private val subscriptionEventStore: SubscriptionEventStore
) {
    fun handle(command: RenewSubscriptionsAutomaticallyCommand): List<SubscriptionEvent> {

        val endedSubscriptionsAsOf = subscriptionEventStore.subscriptionsEnding(LocalDate.parse(command.asOfDate))

        endedSubscriptionsAsOf.map {
            it.renew()
            subscriptionEventStore.store(it.history)
        }

        return endedSubscriptionsAsOf.flatMap { it.history }
    }
}
