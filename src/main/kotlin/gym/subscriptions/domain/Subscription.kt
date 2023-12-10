package gym.subscriptions.domain

import AggregateHistory
import AggregateResult
import DomainEvent
import Id
import Identifiable
import emptyAggregateResult
import gym.membership.domain.EmailAddress
import java.time.LocalDate
import java.time.Period
import kotlin.math.roundToInt

class Subscription private constructor(
    override val id: Id<String>,
    private var threeYearsDiscountApplied: Boolean = false
) : Identifiable<String> {

    internal lateinit var price: Price
    internal lateinit var startDate: LocalDate
    internal lateinit var endDate: LocalDate
    internal lateinit var duration: Duration

    private fun whenEvent(event: DomainEvent) {
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
                threeYearsDiscountApplied = true
            }
        }
    }

    companion object {
        fun subscribe(
            subscriptionId: Id<String>,
            planDurationInMonths: Int,
            subscriptionDate: LocalDate,
            planPrice: Int,
            email: EmailAddress,
            isStudent: Boolean
        ): AggregateResult<String, Subscription, SubscriptionEvent> {
            val subscription = Subscription(subscriptionId)

            val priceAfterDiscount = Price(planPrice)
                .applyDurationDiscount(planDurationInMonths)
                .applyStudentDiscount(isStudent)

            val endDate = subscriptionDate.plusMonths(planDurationInMonths.toLong())

            val event = NewSubscription(
                subscriptionId.toString(),
                priceAfterDiscount.amount,
                Duration(planDurationInMonths).value,
                subscriptionDate.toString(),
                endDate.toString(),
                email.toString(),
                isStudent
            )
            subscription.whenEvent(event)

            return AggregateResult(subscription, event)
        }

        fun restoreFrom(aggregateHistory: AggregateHistory<String, SubscriptionEvent>): Subscription {
            val subscription = Subscription(aggregateHistory.aggregateId)

            aggregateHistory.events.forEach {
                subscription.whenEvent(it)
            }

            return subscription
        }
    }

    fun renew(): AggregateResult<String, Subscription, SubscriptionRenewed> {
        val newEndDate = endDate.plus(Period.ofMonths(duration.value))

        val event = SubscriptionRenewed(
            id.toString(),
            endDate.toString(),
            newEndDate.toString()
        )
        whenEvent(event)

        return AggregateResult(this, event)
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

    fun applyThreeYearsAnniversaryDiscount(date: LocalDate): AggregateResult<String, Subscription, SubscriptionEvent> {
        if (!threeYearsDiscountApplied) {
            val discountedPrice = price.applyThreeYearsAnniversaryDiscount(
                hasThreeYearsAnniversaryOn(date)
            )

            if (price != discountedPrice) {
                val event = SubscriptionDiscountedFor3YearsAnniversary(
                    id.toString(),
                    discountedPrice.amount
                )
                whenEvent(event)

                return AggregateResult(this, event)
            }
        }
        return emptyAggregateResult(this)
    }

    fun hasThreeYearsAnniversaryOn(date: LocalDate): Boolean {
        return date == startDate.plusYears(3)
            && date == endDate
    }
}

internal data class Price private constructor(val amount: Double) {
    init {
        require(amount >= 0) {
            "Price amount must be non-negative, was [$amount]"
        }
    }

    companion object {
        operator fun invoke(value: Number): Price {
            return Price(value.toDouble())
        }
    }

    internal fun applyDurationDiscount(durationInMonths: Int): Price {
        return if (durationInMonths == 12) {
            applyDiscount(0.1)
        } else this
    }

    internal fun applyStudentDiscount(isStudent: Boolean): Price {
        return if (isStudent) {
            applyDiscount(0.2)
        } else this
    }

    internal fun applyThreeYearsAnniversaryDiscount(hasThreeYearsAnniversary: Boolean): Price {
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
