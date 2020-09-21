package gym.reporting.useCases

import gym.reporting.Turnover
import gym.subscriptions.domain.SubscriptionEventStore

class TurnoverForAGivenMonth(
    private val subscriptionEventStore: SubscriptionEventStore
) {
    fun handle(command: TurnoverForAGivenMonthQuery): Turnover {

        return Turnover.monthly(
            subscriptionEventStore.onGoingSubscriptions(command.asOfDate)
        )
    }
}
