package gym.subscriptions.domain

import common.AggregateHistory
import common.AggregateId
import java.time.LocalDate

inline class SubscriptionId(private val id: String) : AggregateId {
    override fun toString(): String = id
}

class Subscription private constructor(val id: SubscriptionId) {

    private lateinit var price: Price
    private lateinit var startDate: LocalDate
    private lateinit var endDate: LocalDate
    private lateinit var duration: Duration

    val recordedEvents: MutableList<SubscriptionEvent> = mutableListOf()

    private fun applyChange(event: SubscriptionEvent) {
        when (event) {
            is NewSubscription -> apply(event)
            is SubscriptionRenewed -> apply(event)
        }

        recordedEvents.add(event)
    }

    private fun apply(event: NewSubscription) {
        this.price = Price(event.subscriptionPrice)
        this.startDate = LocalDate.parse(event.subscriptionStartDate)
        this.endDate = LocalDate.parse(event.subscriptionEndDate)
        this.duration = Duration(event.planDurationInMonths)
    }

    private fun apply(event: SubscriptionRenewed) {
        this.endDate = LocalDate.parse(event.newEndDate)
    }

    companion object {
        fun subscribe(
            subscriptionId: SubscriptionId,
            planDurationInMonths: Int,
            startDate: LocalDate,
            planPrice: Int,
            email: String,
            isStudent: Boolean
        ): Subscription {
            val subscription = Subscription(subscriptionId)
            val priceAfterDiscount = Price(planPrice).applyDiscount(Discount(planDurationInMonths, isStudent))
            val subscriptionEndDate = (startDate.plusMonths(planDurationInMonths.toLong())).minusDays(1)

            subscription.applyChange(
                NewSubscription(
                    subscriptionId.toString(),
                    priceAfterDiscount.amount,
                    Duration(planDurationInMonths).value,
                    startDate.toString(),
                    subscriptionEndDate.toString(),
                    email,
                    isStudent
                )
            )

            return subscription
        }

        fun restoreFrom(aggregateHistory: AggregateHistory): Subscription {
            require(aggregateHistory.events.isNotEmpty()) {
                "Cannot restore without any event."
            }

            val subscription = Subscription(
                SubscriptionId(aggregateHistory.aggregateId.toString())
            )

            aggregateHistory.events.forEach {
                subscription.applyChange(it as SubscriptionEvent)
            }

            return subscription
        }
    }

    fun renew() {
        val newEndDate = (endDate.plusMonths(duration.value.toLong())).minusDays(1)

        applyChange(
            SubscriptionRenewed(
                id.toString(),
                endDate.toString(),
                newEndDate.toString()
            )
        )
    }

    fun isOngoing(asOfDate: LocalDate): Boolean {
        return asOfDate in startDate..endDate
    }

    fun monthlyTurnover(): Int {
        return (price.amount / duration.value)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Subscription

        if (id != other.id) return false
        if (price != other.price) return false
        if (startDate != other.startDate) return false
        if (endDate != other.endDate) return false
        if (duration != other.duration) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + price.hashCode()
        result = 31 * result + startDate.hashCode()
        result = 31 * result + endDate.hashCode()
        result = 31 * result + duration.hashCode()
        return result
    }
}

// invariants

private data class Price(val amount: Int) {
    init {
        require(amount >= 0) {
            "Price amount must be non-negative, was [$amount]"
        }
    }

    internal fun applyDiscount(discount: Discount): Price {
        return Price((amount.toDouble() * (1 - discount.rate)).toInt())
    }
}

internal data class Discount(internal var rate: Double = 0.0) {
    constructor(durationInMonths: Int, isStudent: Boolean) : this() {
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
            "Plan duration is either 1 month or 12 months, was [$value]"
        }
    }
}
