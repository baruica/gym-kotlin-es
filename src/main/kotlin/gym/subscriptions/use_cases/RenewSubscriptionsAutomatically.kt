package gym.subscriptions.use_cases

import gym.subscriptions.domain.SubscriptionEvent
import gym.subscriptions.domain.SubscriptionEventStore
import java.time.LocalDate

class RenewSubscriptionsAutomatically(
    private val eventStore: SubscriptionEventStore
) {
    fun handle(command: RenewSubscriptionsAutomaticallyCommand): List<SubscriptionEvent> {

        val endedSubscriptionsAsOf = eventStore.subscriptionsEnding(LocalDate.parse(command.asOfDate))

        endedSubscriptionsAsOf.map {
            it.renew()
            eventStore.store(it.recordedEvents)
        }

        return endedSubscriptionsAsOf.flatMap { it.recordedEvents }
    }
}
