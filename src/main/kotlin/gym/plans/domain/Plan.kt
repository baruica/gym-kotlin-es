package gym.plans.domain

import common.Aggregate
import common.AggregateHistory
import common.AggregateId
import common.DomainEvent

inline class PlanId(private val id: String) : AggregateId {
    override fun toString(): String = id
}

class Plan private constructor(planId: PlanId) : Aggregate<PlanId>(planId) {

    private lateinit var price: Price
    private lateinit var duration: Duration

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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Plan

        if (id != other.id) return false
        if (price != other.price) return false
        if (duration != other.duration) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + price.hashCode()
        result = 31 * result + duration.hashCode()
        return result
    }
}

private data class Price(val amount: Int) {
    init {
        require(amount >= 0) {
            "Price amount must be non-negative, was [$amount]"
        }
    }
}

private data class Duration(val durationInMonths: Int) {
    init {
        require(listOf(1, 12).contains(durationInMonths)) {
            "Plan duration is either 1 month or 12 months, was [$durationInMonths]"
        }
    }
}
