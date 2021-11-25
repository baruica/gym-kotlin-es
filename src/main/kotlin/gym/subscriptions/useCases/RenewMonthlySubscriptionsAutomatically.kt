package gym.subscriptions.useCases

import DomainEvent
import gym.subscriptions.domain.SubscriptionEventStore
import java.time.LocalDate

data class RenewMonthlySubscriptionsAutomaticallyCommand(val asOfDate: String)

class RenewMonthlySubscriptionsAutomatically(
    private val eventStore: SubscriptionEventStore
) {
    operator fun invoke(command: RenewMonthlySubscriptionsAutomaticallyCommand): List<DomainEvent> {

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
