package gym.subscriptions.domain

import common.EventStore
import java.time.LocalDate

interface SubscriptionEventStore : EventStore {

    fun get(subscriptionId: SubscriptionId): Subscription

    fun subscriptionsEnding(asOfDate: LocalDate): List<Subscription>
}
