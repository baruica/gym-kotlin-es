package gym.subscriptions.useCases

import common.DomainEvent
import gym.subscriptions.domain.SubscriptionEventStore
import java.time.LocalDate

class RenewMonthlySubscriptionsAutomatically(
    private val eventStore: SubscriptionEventStore
) {
    fun handle(command: RenewMonthlySubscriptionsAutomaticallyCommand): List<DomainEvent> {

        val endedMonthlySubscriptionsAsOf = eventStore.endedMonthlySubscriptions(LocalDate.parse(command.asOfDate))

        endedMonthlySubscriptionsAsOf.forEach {
            it.renew()
            eventStore.store(it)
        }

        return endedMonthlySubscriptionsAsOf.flatMap { it.occuredEvents() }
    }
}
