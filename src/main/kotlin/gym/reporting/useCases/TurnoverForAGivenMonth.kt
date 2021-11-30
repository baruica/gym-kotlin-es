package gym.reporting.useCases

import gym.reporting.Turnover
import gym.subscriptions.domain.SubscriptionEventStore
import java.time.LocalDate

data class TurnoverForAGivenMonth(val asOfDate: LocalDate)

class TurnoverForAGivenMonthHandler(
    private val subscriptionEventStore: SubscriptionEventStore
) {
    operator fun invoke(command: TurnoverForAGivenMonth): Turnover {

        return Turnover.monthly(
            subscriptionEventStore.onGoingSubscriptions(command.asOfDate)
        )
    }
}
