package gym.plans.domain

import DomainEvent

sealed class PlanEvent : DomainEvent {
    override fun getAggregateId(): String = planId

    abstract val planId: String
}

data class NewPlanCreated(
    override val planId: String,
    val planPrice: Int,
    val planDurationInMonths: Int
) : PlanEvent()

data class PlanPriceChanged(
    override val planId: String,
    val oldPrice: Int,
    val newPrice: Int
) : PlanEvent()
