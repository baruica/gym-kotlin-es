package gym.reporting.useCases

import gym.subscriptions.domain.SubscriptionEventStore

class TurnoverForAGivenMonth(
    private val subscriptionEventStore: SubscriptionEventStore
) {
    fun handle(command: TurnoverForAGivenMonthQuery): Int {

        return subscriptionEventStore.onGoingSubscriptions(command.asOfDate)
            .map { it.monthlyTurnover() }
            .fold(0, { sum, monthlyTurnover -> sum + monthlyTurnover })
    }
}
