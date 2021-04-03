package gym.subscriptions.domain

import common.Aggregate
import common.AggregateHistory
import common.AggregateId
import common.DomainEvent
import java.time.LocalDate
import java.time.Period

inline class SubscriptionId(private val id: String) : AggregateId {
    override fun toString(): String = id
}

class Subscription private constructor(subscriptionId: SubscriptionId) : Aggregate<SubscriptionId>(subscriptionId) {

    internal lateinit var price: Price
    internal lateinit var startDate: LocalDate
    internal lateinit var endDate: LocalDate
    internal lateinit var duration: Duration

    override fun whenEvent(event: DomainEvent) {
        when (event) {
            is NewSubscription -> {
                price = Price(event.subscriptionPrice)
                startDate = LocalDate.parse(event.subscriptionStartDate)
                endDate = LocalDate.parse(event.subscriptionEndDate)
                duration = Duration(event.planDurationInMonths)
            }
            is SubscriptionRenewed -> {
                endDate = LocalDate.parse(event.newEndDate)
            }
        }
    }

    companion object {
        fun subscribe(
            subscriptionId: String,
            planDurationInMonths: Int,
            subscriptionDate: LocalDate,
            planPrice: Int,
            email: String,
            isStudent: Boolean
        ): Subscription {
            val subscription = Subscription(SubscriptionId(subscriptionId))
            val priceAfterDiscount = Price(planPrice).applyDiscount(planDurationInMonths, isStudent)
            val subscriptionEndDate = (subscriptionDate.plusMonths(planDurationInMonths.toLong())).minusDays(1)

            subscription.applyChange(
                NewSubscription(
                    subscriptionId,
                    priceAfterDiscount.amount,
                    Duration(planDurationInMonths).value,
                    subscriptionDate.toString(),
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
                subscription.whenEvent(it as SubscriptionEvent)
            }

            return subscription
        }
    }

    fun renew() {
        val newEndDate = (endDate.plus(Period.ofMonths(duration.value))).minusDays(1)

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
}

internal data class Price(val amount: Int) {
    init {
        require(amount >= 0) {
            "Price amount must be non-negative, was [$amount]"
        }
    }

    fun applyDiscount(durationInMonths: Int, isStudent: Boolean): Price {
        var rate = 0.0
        if (durationInMonths == 12) {
            rate += 0.1
        }
        if (isStudent) {
            rate += 0.2
        }
        return Price((amount.toDouble() * (1 - rate)).toInt())
    }
}

internal data class Duration(val value: Int) {
    init {
        require(listOf(1, 12).contains(value)) {
            "Plan duration is either 1 month or 12 months, was [$value]"
        }
    }
}
