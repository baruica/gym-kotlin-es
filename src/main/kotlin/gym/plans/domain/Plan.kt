package gym.plans.domain

import common.Aggregate
import common.AggregateHistory
import common.AggregateId
import common.DomainEvent

@JvmInline
value class PlanId(private val id: String) : AggregateId {
    override fun toString(): String = id
}

class Plan private constructor(planId: PlanId) : Aggregate<PlanId>(planId) {

    internal lateinit var price: Price
    internal lateinit var duration: Duration

    override fun whenEvent(event: DomainEvent) {
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
            id: String,
            priceAmount: Int,
            durationInMonths: Int
        ): Plan {
            val plan = Plan(PlanId(id))
            val price = Price(priceAmount)
            val duration = Duration(durationInMonths)

            plan.applyChange(
                NewPlanCreated(
                    plan.id.toString(),
                    price.amount,
                    duration.durationInMonths
                )
            )

            return plan
        }

        fun restoreFrom(aggregateHistory: AggregateHistory): Plan {
            require(aggregateHistory.events.isNotEmpty()) {
                "Cannot restore without any event."
            }

            val plan = Plan(
                PlanId(aggregateHistory.aggregateId.toString())
            )

            aggregateHistory.events.forEach {
                plan.whenEvent(it as PlanEvent)
            }

            return plan
        }
    }

    fun changePrice(newPriceAmount: Int) {
        val newPrice = Price(newPriceAmount)

        if (price != newPrice) {
            applyChange(
                PlanPriceChanged(
                    id.toString(),
                    price.amount,
                    newPrice.amount
                )
            )
        }
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
