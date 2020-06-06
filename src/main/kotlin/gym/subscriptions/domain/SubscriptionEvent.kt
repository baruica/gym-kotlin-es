package gym.subscriptions.domain

import common.DomainEvent
import java.time.Instant

sealed class SubscriptionEvent : DomainEvent {
    override fun getAggregateId(): String = subscriptionId
    override val created: Instant = Instant.now()

    abstract val subscriptionId: String

    fun getEndDate(): String {
        return when (this) {
            is NewSubscription -> subscriptionEndDate
            is SubscriptionRenewed -> newEndDate
        }
    }
}

data class NewSubscription(
    override val subscriptionId: String,
    val subscriptionPrice: Int,
    val planDurationInMonths: Int,
    val subscriptionStartDate: String,
    val subscriptionEndDate: String,
    val email: String,
    val isStudent: Boolean
) : SubscriptionEvent()

data class SubscriptionRenewed(
    override val subscriptionId: String,
    val oldEndDate: String,
    val newEndDate: String
) : SubscriptionEvent()
