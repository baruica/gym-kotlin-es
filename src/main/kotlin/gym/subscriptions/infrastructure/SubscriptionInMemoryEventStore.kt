package gym.subscriptions.infrastructure

import gym.subscriptions.domain.Subscription
import gym.subscriptions.domain.SubscriptionEvent
import gym.subscriptions.domain.SubscriptionEvent.NewSubscription
import gym.subscriptions.domain.SubscriptionEventStore
import gym.subscriptions.domain.SubscriptionId
import java.time.LocalDate
import java.util.*

class SubscriptionInMemoryEventStore : SubscriptionEventStore {

    private val events = HashMap<SubscriptionId, MutableList<SubscriptionEvent>>()

    override fun nextId(): SubscriptionId {
        return SubscriptionId(UUID.randomUUID().toString())
    }

    override fun store(events: List<SubscriptionEvent>) {
        events.forEach {
            this.events.getOrPut(SubscriptionId(it.subscriptionId)) { mutableListOf() }.add(it)
        }
    }

    override fun getAllEvents(subscriptionId: SubscriptionId): List<SubscriptionEvent> {
        return getAggregateEvents(subscriptionId)
    }

    override fun get(subscriptionId: SubscriptionId): Subscription {
        return Subscription.restoreFrom(getAggregateEvents(subscriptionId))
    }

    override fun subscriptionsEnding(asOfDate: LocalDate): List<Subscription> {
        val subscriptionsEnding = mutableListOf<Subscription>()

        events.values.forEach { subscriptionEvents ->
            subscriptionEvents.forEach { subscriptionEvent ->
                if (subscriptionEvent is NewSubscription) {
                    if (LocalDate.parse(subscriptionEvent.subscriptionEndDate) == asOfDate) {
                        subscriptionsEnding.add(
                            Subscription.restoreFrom(
                                this.events[SubscriptionId(subscriptionEvent.subscriptionId)]!!.toList()
                            )
                        )
                    }
                }
            }
        }

        return subscriptionsEnding
    }

    private fun getAggregateEvents(id: SubscriptionId): MutableList<SubscriptionEvent> = this.events.getOrDefault(id, mutableListOf())
}
