package gym.subscriptions.useCases

import DomainEvent
import gym.subscriptions.domain.Subscription
import gym.subscriptions.domain.SubscriptionEventStore
import java.time.LocalDate

class SubscribeToPlan(
    private val eventStore: SubscriptionEventStore
) {
    operator fun invoke(command: SubscribeToPlanCommand): List<DomainEvent> {

        val subscription = Subscription.subscribe(
            command.subscriptionId,
            command.planDurationInMonths,
            LocalDate.parse(command.startDate),
            command.planPrice,
            command.email,
            command.isStudent
        )

        eventStore.store(subscription)

        return subscription.recentEvents()
    }
}
