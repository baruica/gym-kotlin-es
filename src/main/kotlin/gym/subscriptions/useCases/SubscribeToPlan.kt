package gym.subscriptions.useCases

import gym.subscriptions.domain.Subscription
import gym.subscriptions.domain.SubscriptionEvent
import gym.subscriptions.domain.SubscriptionEventStore
import java.time.LocalDate

data class SubscribeToPlanCommand(
    val subscriptionId: String,
    val planPrice: Int,
    val planDurationInMonths: Int,
    val startDate: String,
    val isStudent: Boolean,
    val email: String,
)

class SubscribeToPlan(
    private val eventStore: SubscriptionEventStore
) {
    operator fun invoke(command: SubscribeToPlanCommand): SubscriptionEvent {

        val aggregateResult = Subscription.subscribe(
            command.subscriptionId,
            command.planDurationInMonths,
            LocalDate.parse(command.startDate),
            command.planPrice,
            command.email,
            command.isStudent
        )

        eventStore.store(aggregateResult)

        return aggregateResult.event as SubscriptionEvent
    }
}
