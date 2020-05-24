package gym.plans.domain

import common.AggregateHistory
import common.AggregateId

inline class PlanId(private val id: String) : AggregateId {
    override fun toString(): String = id
}

class Plan private constructor(val planId: PlanId) {

    private lateinit var price: Price
    private lateinit var durationInMonths: Duration

    constructor(planId: PlanId, priceAmount: Int, durationInMonths: Int) : this(planId) {
        recordEvent(
            NewPlanCreated(
                planId.toString(),
                Price(priceAmount).value,
                Duration(durationInMonths).value
            )
        )
    }

    val history: MutableList<PlanEvent> = mutableListOf()

    private fun recordEvent(event: PlanEvent) {
        when (event) {
            is NewPlanCreated -> apply(event)
            is PlanPriceChanged -> apply(event)
        }

        history.add(event)
    }

    private fun apply(event: NewPlanCreated) {
        this.price = Price(event.planPrice)
        this.durationInMonths = Duration(event.planDurationInMonths)
    }

    private fun apply(event: PlanPriceChanged) {
        this.price = Price(event.newPrice)
    }

    companion object {
        fun restoreFrom(aggregateHistory: AggregateHistory): Plan {
            require(aggregateHistory.events.isNotEmpty()) {
                "Cannot restore without any event."
            }

            val plan = Plan(aggregateHistory.aggregateId as PlanId)

            aggregateHistory.events.forEach {
                plan.recordEvent(it as PlanEvent)
            }

            return plan
        }
    }

    fun changePrice(newPriceAmount: Int) {
        val newPrice = Price(newPriceAmount)
        val latestPrice = latestPrice()

        if (latestPrice != newPrice) {
            recordEvent(
                PlanPriceChanged(
                    this.planId.toString(),
                    latestPrice.value,
                    newPrice.value
                )
            )
        }
    }

    private fun latestPrice(): Price {
        return Price(history.last().getPrice())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Plan

        if (planId != other.planId) return false
        if (price != other.price) return false
        if (durationInMonths != other.durationInMonths) return false

        return true
    }

    override fun hashCode(): Int {
        var result = planId.hashCode()
        result = 31 * result + price.hashCode()
        result = 31 * result + durationInMonths.hashCode()
        return result
    }
}

private data class Price(val value: Int) {
    init {
        require(value >= 0) {
            "Price amount must be non-negative, was $value"
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
