package gym.subscriptions.use_cases

import gym.subscriptions.domain.Subscription
import gym.subscriptions.domain.SubscriptionEvent
import gym.subscriptions.domain.SubscriptionEventStore
import gym.subscriptions.domain.SubscriptionId
import java.time.LocalDate

class SubscribeToPlan(
    private val subscriptionRepository: SubscriptionEventStore
) {
    fun handle(command: SubscribeToPlanCommand): List<SubscriptionEvent> {

        val subscription = Subscription(
            SubscriptionId(command.subscriptionId),
            command.planPrice,
            LocalDate.parse(command.startDate),
            command.planDurationInMonths,
            command.email,
            command.isStudent
        )

        subscriptionRepository.store(subscription.history)

        return subscription.history
    }
}
