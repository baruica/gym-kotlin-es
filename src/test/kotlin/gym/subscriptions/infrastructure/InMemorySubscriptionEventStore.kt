package gym.subscriptions.infrastructure

import InMemoryEventStore
import gym.subscriptions.domain.Subscription
import gym.subscriptions.domain.SubscriptionEvent
import gym.subscriptions.domain.SubscriptionEventStore
import java.time.LocalDate

class InMemorySubscriptionEventStore : InMemoryEventStore<String, Subscription, SubscriptionEvent>(), SubscriptionEventStore {

    override fun get(subscriptionId: String): Subscription {
        return Subscription.restoreFrom(
            getAggregateHistory(subscriptionId)
        )
    }

    override fun endedMonthlySubscriptions(date: LocalDate): List<Subscription> {
        return events.keys
            .map { subscriptionId -> get(subscriptionId) }
            .filter { subscription -> subscription.isEndedMonthly(date) }
    }

    override fun threeYearsAnniversarySubscriptions(date: LocalDate): List<Subscription> {
        return events.keys
            .map { subscriptionId -> get(subscriptionId) }
            .filter { subscription -> subscription.hasThreeYearsAnniversaryOn(date) }
    }

    override fun onGoingSubscriptions(date: LocalDate): List<Subscription> {
        return events.keys
            .map { subscriptionId -> get(subscriptionId) }
            .filter { subscription -> subscription.isOngoing(date) }
    }
}
