package gym.subscriptions.domain

import Aggregate
import AggregateHistory
import AggregateId
import DomainEvent
import java.time.LocalDate
import java.time.Period
import kotlin.math.roundToInt

class SubscriptionId(subscriptionId: String) : AggregateId(subscriptionId)

class Subscription private constructor(subscriptionId: String) : Aggregate(subscriptionId) {

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
            is SubscriptionDiscountedFor3YearsAnniversary -> {
                price = Price(event.discountedPrice)
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
            val subscription = Subscription(subscriptionId)

            val priceAfterDiscount = Price(planPrice)
                .applyDurationDiscount(planDurationInMonths)
                .applyStudentDiscount(isStudent)

            val endDate = subscriptionDate.plusMonths(planDurationInMonths.toLong())

            subscription.applyChange(
                NewSubscription(
                    subscriptionId,
                    priceAfterDiscount.amount,
                    Duration(planDurationInMonths).value,
                    subscriptionDate.toString(),
                    endDate.toString(),
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

            val subscription = Subscription(aggregateHistory.aggregateId)

            aggregateHistory.events.forEach {
                subscription.whenEvent(it as SubscriptionEvent)
            }

            return subscription
        }
    }

    fun renew() {
        val newEndDate = endDate.plus(Period.ofMonths(duration.value))

        applyChange(
            SubscriptionRenewed(
                id.toString(),
                endDate.toString(),
                newEndDate.toString()
            )
        )
    }

    fun isOngoing(date: LocalDate): Boolean {
        return date in startDate..endDate
    }

    fun isEndedMonthly(date: LocalDate): Boolean {
        return endDate == date && duration.isMonthly()
    }

    fun monthlyTurnover(): Int {
        return (price.amount / duration.value).roundToInt()
    }

    fun applyThreeYearsAnniversaryDiscount(date: LocalDate) {
        if (threeYearsDiscountNotYetApplied()) {
            val discountedPrice = price.applyThreeYearsAnniversaryDiscount(
                hasThreeYearsAnniversaryOn(date)
            )

            if (price != discountedPrice) {
                applyChange(
                    SubscriptionDiscountedFor3YearsAnniversary(
                        id.toString(),
                        discountedPrice.amount
                    )
                )
            }
        }
    }

    fun hasThreeYearsAnniversaryOn(date: LocalDate): Boolean {
        return date == startDate.plusYears(3)
            && date == endDate
    }

    private fun threeYearsDiscountNotYetApplied(): Boolean {
        return !this.events.contains(
            SubscriptionDiscountedFor3YearsAnniversary(
                id.toString(),
                price.amount
            )
        )
    }
}

internal data class Price(val amount: Double) {
    constructor(amount: Int) : this(amount.toDouble())

    init {
        require(amount >= 0) {
            "Price amount must be non-negative, was [$amount]"
        }
    }

    fun applyDurationDiscount(durationInMonths: Int): Price {
        return if (durationInMonths == 12) {
            applyDiscount(0.1)
        } else this
    }

    fun applyStudentDiscount(isStudent: Boolean): Price {
        return if (isStudent) {
            applyDiscount(0.2)
        } else this
    }

    fun applyThreeYearsAnniversaryDiscount(hasThreeYearsAnniversary: Boolean): Price {
        return if (hasThreeYearsAnniversary) {
            applyDiscount(0.05)
        } else this
    }

    private fun applyDiscount(rate: Double): Price {
        return Price(amount * (1 - rate))
    }
}

internal data class Duration(val value: Int) {
    init {
        require(listOf(1, 12).contains(value)) {
            "Plan duration is either 1 month or 12 months, was [$value]"
        }
    }

    fun isMonthly(): Boolean {
        return value == 1
    }
}
