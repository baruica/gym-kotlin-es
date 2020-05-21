package gym.subscriptions.domain

import gym.subscriptions.domain.SubscriptionEvent.NewSubscription
import gym.subscriptions.domain.SubscriptionEvent.SubscriptionRenewed
import java.time.LocalDate

inline class SubscriptionId(private val id: String) {
    override fun toString(): String = id
}

class Subscription private constructor(val subscriptionId: SubscriptionId) {

    private lateinit var price: Price
    private lateinit var startDate: LocalDate
    private lateinit var endDate: LocalDate
    private lateinit var durationInMonths: Duration

    constructor(
        subscriptionId: SubscriptionId,
        startDate: LocalDate,
        planDurationInMonths: Int,
        price: Int,
        email: String,
        isStudent: Boolean
    ) : this(subscriptionId) {

        recordEvent(
            NewSubscription(
                subscriptionId.toString(),
                Price(price).afterDiscount(planDurationInMonths, isStudent),
                planDurationInMonths,
                startDate.toString(),
                (startDate.plusMonths(planDurationInMonths.toLong())).minusDays(1).toString(),
                email,
                isStudent
            )
        )
    }

    val history: MutableList<SubscriptionEvent> = mutableListOf()

    private fun recordEvent(event: SubscriptionEvent) {
        when (event) {
            is NewSubscription -> apply(event)
            is SubscriptionRenewed -> apply(event)
        }

        history.add(event)
    }

    private fun apply(event: NewSubscription) {
        this.price = Price(event.subscriptionPrice)
        this.startDate = LocalDate.parse(event.subscriptionStartDate)
        this.endDate = LocalDate.parse(event.subscriptionEndDate)
        this.durationInMonths = Duration(event.planDurationInMonths)
    }

    private fun apply(event: SubscriptionRenewed) {
        this.endDate = LocalDate.parse(event.newEndDate)
    }

    companion object {
        fun restoreFrom(events: List<SubscriptionEvent>): Subscription {
            require(events.isNotEmpty()) {
                "Cannot restore without any event."
            }

            val subscription = Subscription(
                SubscriptionId(events.last().subscriptionId)
            )

            events.forEach {
                when (it) {
                    is NewSubscription -> subscription.apply(it)
                    is SubscriptionRenewed -> subscription.apply(it)
                }
            }

            return subscription
        }
    }

    fun renew() {
        val newEndDate = (endDate.plusMonths(durationInMonths.value.toLong())).minusDays(1)

        recordEvent(
            SubscriptionRenewed(
                subscriptionId.toString(),
                endDate.toString(),
                newEndDate.toString()
            )
        )
    }

    fun monthlyTurnover(): Double {
        return (price.amount / durationInMonths.value).toDouble()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Subscription

        if (subscriptionId != other.subscriptionId) return false
        if (price != other.price) return false
        if (startDate != other.startDate) return false
        if (endDate != other.endDate) return false
        if (durationInMonths != other.durationInMonths) return false

        return true
    }

    override fun hashCode(): Int {
        var result = subscriptionId.hashCode()
        result = 31 * result + price.hashCode()
        result = 31 * result + startDate.hashCode()
        result = 31 * result + endDate.hashCode()
        result = 31 * result + durationInMonths.hashCode()
        return result
    }
}

private data class Price(val amount: Int) {
    init {
        require(amount >= 0) {
            "Price amount must be non-negative, was $amount"
        }
    }

    internal fun afterDiscount(durationInMonths: Int, isStudent: Boolean): Int {
        return (amount.toDouble() * (1 - Discount(durationInMonths, isStudent).rate)).toInt()
    }
}

private class Discount(durationInMonths: Int, isStudent: Boolean) {
    internal var rate: Double = 0.0

    init {
        if (durationInMonths == 12) {
            rate += 0.3
        }
        if (isStudent) {
            rate += 0.2
        }
    }
}

private data class Duration(val value: Int) {
    init {
        require(listOf(1, 12).contains(value)) {
            "Plan duration is either 1 month or 12 months, was $value"
        }
    }
}
