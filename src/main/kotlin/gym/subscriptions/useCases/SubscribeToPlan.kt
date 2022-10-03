package gym.subscriptions.useCases

import DomainEvent
import gym.membership.domain.EmailAddress
import gym.subscriptions.domain.Subscription
import gym.subscriptions.domain.SubscriptionEventStore
import gym.subscriptions.domain.SubscriptionId
import java.time.LocalDate

data class SubscribeToPlan(
    val subscriptionId: SubscriptionId,
    val planPrice: Int,
    val planDurationInMonths: Int,
    val startDate: LocalDate,
    val isStudent: Boolean,
    val email: EmailAddress,
) {
    class Handler(
        private val eventStore: SubscriptionEventStore
    ) {
        operator fun invoke(command: SubscribeToPlan): List<DomainEvent> {

            return Subscription.subscribe(
                command.subscriptionId,
                command.planDurationInMonths,
                command.startDate,
                command.planPrice,
                command.email,
                command.isStudent
            )
                .also { eventStore.store(it) }
                .events
        }
    }
}
