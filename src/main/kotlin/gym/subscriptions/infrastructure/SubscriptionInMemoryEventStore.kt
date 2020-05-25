package gym.subscriptions.infrastructure

import common.AggregateHistory
import common.AggregateId
import common.DomainEvent
import gym.subscriptions.domain.*
import java.time.LocalDate
import java.util.*

class SubscriptionInMemoryEventStore : SubscriptionEventStore {

    private val events = mutableMapOf<SubscriptionId, MutableList<SubscriptionEvent>>()

    override fun nextId(): String {
        return UUID.randomUUID().toString()
    }

    override fun store(events: List<DomainEvent>) {
        events.forEach {
            this.events.getOrPut(SubscriptionId(it.aggregateId())) { mutableListOf() }.add(it as SubscriptionEvent)
        }
    }

    override fun getAggregateHistoryFor(aggregateId: AggregateId): AggregateHistory {
        return AggregateHistory(
            aggregateId,
            getAggregateEvents(aggregateId)
        )
    }

    override fun get(subscriptionId: SubscriptionId): Subscription {
        return Subscription.restoreFrom(getAggregateHistoryFor(subscriptionId))
    }

    override fun subscriptionsEnding(asOfDate: LocalDate): List<Subscription> {
        val subscriptionsEnding = mutableListOf<Subscription>()

        events.values.forEach { subscriptionEvents ->
            subscriptionEvents.forEach { subscriptionEvent ->
                if (subscriptionEvent is NewSubscription) {
                    if (LocalDate.parse(subscriptionEvent.subscriptionEndDate) == asOfDate) {
                        val subscriptionId = SubscriptionId(subscriptionEvent.subscriptionId)

                        subscriptionsEnding.add(
                            Subscription.restoreFrom(
                                AggregateHistory(subscriptionId, this.events[subscriptionId]!!.toList())
                            )
                        )
                    }
                }
            }
        }

        return subscriptionsEnding
    }

    private fun getAggregateEvents(id: AggregateId): MutableList<SubscriptionEvent> =
        this.events.getOrDefault(id as SubscriptionId, mutableListOf())
}
