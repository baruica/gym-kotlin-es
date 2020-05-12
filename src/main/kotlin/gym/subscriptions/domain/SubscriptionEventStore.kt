package gym.subscriptions.domain

import java.time.LocalDate

interface SubscriptionEventStore {

    fun nextId(): SubscriptionId

    fun store(events: List<SubscriptionEvent>)

    fun get(subscriptionId: SubscriptionId): Subscription

    fun getAllEvents(subscriptionId: SubscriptionId): List<SubscriptionEvent>

    fun subscriptionsEnding(asOfDate: LocalDate): List<Subscription>
}
