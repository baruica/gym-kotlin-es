package gym.plans.domain

import AggregateHistory
import AggregateResult
import DomainEvent
import Id
import Identifiable
import emptyAggregateResult

class Plan private constructor(
    override val id: Id<String>
) : Identifiable<String> {

    internal lateinit var price: Price
    internal lateinit var duration: Duration

    private fun whenEvent(event: DomainEvent) {
        when (event) {
            is NewPlanCreated -> {
                price = Price(event.planPrice)
                duration = Duration(event.planDurationInMonths)
            }

            is PlanPriceChanged -> {
                price = Price(event.newPrice)
            }
        }
    }

    companion object {
        fun new(
            id: Id<String>,
            priceAmount: Int,
            durationInMonths: Int
        ): AggregateResult<String, Plan, NewPlanCreated> {
            val plan = Plan(id)
            val price = Price(priceAmount)
            val duration = Duration(durationInMonths)

            val event = NewPlanCreated(
                plan.id.toString(),
                price.amount,
                duration.durationInMonths
            )
            plan.whenEvent(event)

            return AggregateResult(plan, event)
        }

        fun restoreFrom(aggregateHistory: AggregateHistory<String, PlanEvent>): Plan {
            val plan = Plan(aggregateHistory.aggregateId)

            aggregateHistory.events.forEach {
                plan.whenEvent(it)
            }

            return plan
        }
    }

    fun changePrice(newPriceAmount: Int): AggregateResult<String, Plan, PlanPriceChanged> {
        val newPrice = Price(newPriceAmount)

        if (price != newPrice) {
            val event = PlanPriceChanged(
                id.toString(),
                price.amount,
                newPrice.amount
            )
            whenEvent(event)

            return AggregateResult(this, event)
        }

        return emptyAggregateResult(this)
    }
}

internal data class Price(val amount: Int) {
    init {
        require(amount >= 0) {
            "Price amount must be non-negative, was [$amount]"
        }
    }
}

internal data class Duration(val durationInMonths: Int) {
    init {
        require(listOf(1, 12).contains(durationInMonths)) {
            "Plan duration is either 1 month or 12 months, was [$durationInMonths]"
        }
    }
}
