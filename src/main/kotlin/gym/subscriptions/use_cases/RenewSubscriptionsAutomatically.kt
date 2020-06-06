package gym.subscriptions.use_cases

import common.DomainEvent
import gym.subscriptions.domain.SubscriptionEventStore
import java.time.LocalDate

class RenewSubscriptionsAutomatically(
    private val eventStore: SubscriptionEventStore
) {
    fun handle(command: RenewSubscriptionsAutomaticallyCommand): List<DomainEvent> {

        val endedSubscriptionsAsOf = eventStore.subscriptionsEnding(LocalDate.parse(command.asOfDate))

        endedSubscriptionsAsOf.map {
            it.renew()
            eventStore.store(it)
        }

        return endedSubscriptionsAsOf.flatMap { it.occuredEvents() }
    }
}
