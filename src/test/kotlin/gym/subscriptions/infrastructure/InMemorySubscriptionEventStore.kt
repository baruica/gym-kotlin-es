package gym.subscriptions.infrastructure

import common.AggregateHistory
import common.AggregateId
import common.DomainEvent
import gym.subscriptions.domain.*
import java.time.LocalDate

class InMemorySubscriptionEventStore : SubscriptionEventStore {

    private val events = mutableMapOf<SubscriptionId, MutableList<SubscriptionEvent>>()

    override fun store(events: List<DomainEvent>) {
        events.forEach {
            this.events.getOrPut(SubscriptionId(it.getAggregateId())) { mutableListOf() }.add(it as SubscriptionEvent)
        }
    }

    override fun get(subscriptionId: SubscriptionId): Subscription {
        return Subscription.restoreFrom(getAggregateHistory(subscriptionId))
    }

    override fun getAggregateEvents(aggregateId: AggregateId): MutableList<SubscriptionEvent> =
        this.events.getOrDefault(aggregateId as SubscriptionId, mutableListOf())

    override fun subscriptionsEnding(date: LocalDate): List<Subscription> {
        val subscriptionsEnding = mutableListOf<Subscription>()

        events.values.forEach { subscriptionEvents ->
            subscriptionEvents.forEach { subscriptionEvent ->
                if (subscriptionEvent is NewSubscription) {
                    if (LocalDate.parse(subscriptionEvent.subscriptionEndDate) == date) {
                        subscriptionsEnding.add(
                            restoreSubscription(subscriptionEvent.getAggregateId())
                        )
                    }
                }
            }
        }

        return subscriptionsEnding
    }

    override fun onGoingSubscriptions(date: LocalDate): List<Subscription> {
        val subscriptionsThatStartedBeforeDate = mutableListOf<Subscription>()

        events.values.forEach { subscriptionEvents ->
            subscriptionEvents.forEach { subscriptionEvent ->
                if (subscriptionEvent is NewSubscription) {
                    val startDate = LocalDate.parse(subscriptionEvent.subscriptionStartDate)
                    if (startDate.isBefore(date) || startDate.isEqual(date)) {
                        subscriptionsThatStartedBeforeDate.add(
                            restoreSubscription(subscriptionEvent.getAggregateId())
                        )
                    }
                }
            }
        }

        return subscriptionsThatStartedBeforeDate.filter {
            it.isOngoing(date)
        }
    }

    private fun restoreSubscription(aggregateId: String): Subscription {
        val subscriptionId = SubscriptionId(aggregateId)

        return Subscription.restoreFrom(
            AggregateHistory(subscriptionId, this.events[subscriptionId]!!.toList())
        )
    }
}
