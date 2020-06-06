package gym.subscriptions.use_cases

import common.DomainEvent
import gym.subscriptions.domain.Subscription
import gym.subscriptions.domain.SubscriptionEventStore
import gym.subscriptions.domain.SubscriptionId
import java.time.LocalDate

class SubscribeToPlan(
    private val eventStore: SubscriptionEventStore
) {
    fun handle(command: SubscribeToPlanCommand): List<DomainEvent> {

        val subscription = Subscription.subscribe(
            SubscriptionId(command.subscriptionId),
            command.planDurationInMonths,
            LocalDate.parse(command.startDate),
            command.planPrice,
            command.email,
            command.isStudent
        )

        eventStore.store(subscription)

        return subscription.occuredEvents()
    }
}
