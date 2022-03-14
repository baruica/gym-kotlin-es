package gym.subscriptions.useCases

import DomainEvent
import gym.subscriptions.domain.SubscriptionEventStore
import java.time.LocalDate

data class RenewMonthlySubscriptionsAutomatically(val asOfDate: String) {
    class Handler(
        private val eventStore: SubscriptionEventStore
    ) {
        operator fun invoke(command: RenewMonthlySubscriptionsAutomatically): List<DomainEvent> {

            val endedMonthlySubscriptionsAsOf = eventStore.endedMonthlySubscriptions(LocalDate.parse(command.asOfDate))

            val events = mutableListOf<DomainEvent>()

            endedMonthlySubscriptionsAsOf.forEach {
                val aggregateResult = it.renew()
                eventStore.store(aggregateResult)
                events.addAll(aggregateResult.events)
            }

            return events
        }
    }
}
