package gym.subscriptions.useCases

import gym.subscriptions.domain.SubscriptionEvent
import gym.subscriptions.domain.SubscriptionEventStore
import java.time.LocalDate

data class RenewMonthlySubscriptionsAutomaticallyCommand(val asOfDate: String)

class RenewMonthlySubscriptionsAutomatically(
    private val eventStore: SubscriptionEventStore
) {
    operator fun invoke(command: RenewMonthlySubscriptionsAutomaticallyCommand): List<SubscriptionEvent> {

        val endedMonthlySubscriptionsAsOf = eventStore.endedMonthlySubscriptions(LocalDate.parse(command.asOfDate))

        endedMonthlySubscriptionsAsOf.forEach {
            val aggregateResult = it.renew()
            eventStore.store(aggregateResult)
        }

        return endedMonthlySubscriptionsAsOf.flatMap { it.recentEvents() }
    }
}
