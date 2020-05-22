package gym.subscriptions.infrastructure

import gym.subscriptions.domain.Subscription
import gym.subscriptions.domain.SubscriptionId
import gym.subscriptions.domain.SubscriptionRepository
import java.time.LocalDate
import java.util.*

class SubscriptionInMemoryRepository : SubscriptionRepository {

    private val subscriptions = mutableMapOf<SubscriptionId, Subscription>()

    override fun nextId(): SubscriptionId {
        return SubscriptionId(UUID.randomUUID().toString())
    }

    override fun store(subscription: Subscription) {
        subscriptions[subscription.subscriptionId] = subscription
    }

    override fun storeAll(subscriptions: Map<SubscriptionId, Subscription>) {
        subscriptions.forEach {
            store(it.value)
        }
    }

    override fun get(subscriptionId: SubscriptionId): Subscription {
        return subscriptions[subscriptionId]
            ?: throw SubscriptionRepositoryException.notFound(subscriptionId)
    }

    override fun endedSubscriptions(asOfDate: LocalDate): Map<SubscriptionId, Subscription> {
        return subscriptions.filterValues {
            true // TODO
        }
    }

    override fun onGoingSubscriptions(asOfDate: LocalDate): Map<SubscriptionId, Subscription> {
        return subscriptions.filterValues {
            true // TODO
        }
    }
}
