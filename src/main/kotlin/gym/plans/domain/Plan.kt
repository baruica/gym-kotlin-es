package gym.plans.domain

import Aggregate
import AggregateHistory
import AggregateResult
import DomainEvent

@JvmInline
value class PlanId(private val id: String) {
    override fun toString(): String = id
}

class Plan private constructor(
    private val id: PlanId
) : Aggregate {

    internal lateinit var price: Price
    internal lateinit var duration: Duration

    override fun getId(): String = id.toString()

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
            id: PlanId,
            priceAmount: Int,
            durationInMonths: Int
        ): AggregateResult<Plan, NewPlanCreated> {
            val plan = Plan(id)
            val price = Price(priceAmount)
            val duration = Duration(durationInMonths)

            val event = NewPlanCreated(
                plan.getId(),
                price.amount,
                duration.durationInMonths
            )
            plan.whenEvent(event)

            return AggregateResult.of(plan, event)
        }

        fun restoreFrom(aggregateHistory: AggregateHistory<PlanEvent>): Plan {
            val plan = Plan(PlanId(aggregateHistory.aggregateId))

            aggregateHistory.events.forEach {
                plan.whenEvent(it)
            }

            return plan
        }
    }

    fun changePrice(newPriceAmount: Int): AggregateResult<Plan, PlanPriceChanged> {
        val newPrice = Price(newPriceAmount)

        if (price != newPrice) {
            val event = PlanPriceChanged(
                getId(),
                price.amount,
                newPrice.amount
            )
            whenEvent(event)

            return AggregateResult.of(this, event)
        }

        return AggregateResult.empty(this)
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
