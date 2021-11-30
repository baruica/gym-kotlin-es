package gym.subscriptions.useCases

import DomainEvent
import gym.subscriptions.domain.Subscription
import gym.subscriptions.domain.SubscriptionEventStore
import java.time.LocalDate

data class SubscribeToPlan(
    val subscriptionId: String,
    val planPrice: Int,
    val planDurationInMonths: Int,
    val startDate: String,
    val isStudent: Boolean,
    val email: String,
)

class SubscribeToPlanHandler(
    private val eventStore: SubscriptionEventStore
) {
    operator fun invoke(command: SubscribeToPlan): List<DomainEvent> {

        val aggregateResult = Subscription.subscribe(
            command.subscriptionId,
            command.planDurationInMonths,
            LocalDate.parse(command.startDate),
            command.planPrice,
            command.email,
            command.isStudent
        )

        eventStore.store(aggregateResult)

        return aggregateResult.events
    }
}
