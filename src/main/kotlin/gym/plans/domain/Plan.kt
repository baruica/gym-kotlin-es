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

        val planPrice = Price(priceAmount)
        val planDurationInMonths = Duration(durationInMonths)

        recordEvent(
            NewPlanCreated(
                planId.toString(),
                planPrice.amount,
                planDurationInMonths.durationInMonths
            )
        )
    }

    val recordedEvents: MutableList<PlanEvent> = mutableListOf()

    private fun recordEvent(event: PlanEvent) {
        recordedEvents.add(event)

        apply(event)
    }

    private fun apply(event: PlanEvent) {
        when (event) {
            is NewPlanCreated -> apply(event)
            is PlanPriceChanged -> apply(event)
        }
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

            val plan = Plan(
                PlanId(aggregateHistory.aggregateId.toString())
            )

            aggregateHistory.events.forEach {
                plan.apply(it as PlanEvent)
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
                    latestPrice.amount,
                    newPrice.amount
                )
            )
        }
    }

    private fun latestPrice(): Price {
        return this.price
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

// invariants

private data class Price(val amount: Int) {
    init {
        require(amount >= 0) {
            "Price amount must be non-negative, was $amount"
        }
    }
}

private data class Duration(val durationInMonths: Int) {
    init {
        require(listOf(1, 12).contains(durationInMonths)) {
            "Plan duration is either 1 month or 12 months, was $durationInMonths"
        }
    }
}
