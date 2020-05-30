package gym.subscriptions.domain

import common.EventStore
import java.time.LocalDate

interface SubscriptionEventStore : EventStore {

    fun get(subscriptionId: SubscriptionId): Subscription

    fun subscriptionsEnding(date: LocalDate): List<Subscription>

    fun onGoingSubscriptions(date: LocalDate): List<Subscription>
}
