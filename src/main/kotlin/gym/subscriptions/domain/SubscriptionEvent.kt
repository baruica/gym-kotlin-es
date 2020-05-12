package gym.subscriptions.domain

sealed class SubscriptionEvent(open val subscriptionId: String) {

    data class NewSubscription(
        override val subscriptionId: String,
        val subscriptionPrice: Int,
        val planDurationInMonths: Int,
        val subscriptionStartDate: String,
        val subscriptionEndDate: String,
        val email: String,
        val isStudent: Boolean
    ) : SubscriptionEvent(subscriptionId)

    data class SubscriptionRenewed(
        override val subscriptionId: String,
        val oldEndDate: String,
        val newEndDate: String
    ) : SubscriptionEvent(subscriptionId)

    fun getEndDate(): String {
        return when (this) {
            is NewSubscription -> subscriptionEndDate
            is SubscriptionRenewed -> newEndDate
        }
    }
}
