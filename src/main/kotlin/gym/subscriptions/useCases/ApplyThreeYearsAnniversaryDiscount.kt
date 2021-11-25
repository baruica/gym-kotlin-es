package gym.subscriptions.useCases

import DomainEvent
import gym.subscriptions.domain.SubscriptionEventStore
import java.time.LocalDate

class ApplyThreeYearsAnniversaryDiscountCommand(val asOfDate: String)

class ApplyThreeYearsAnniversaryDiscount(
    private val eventStore: SubscriptionEventStore
) {
    operator fun invoke(command: ApplyThreeYearsAnniversaryDiscountCommand): List<DomainEvent> {

        val date = LocalDate.parse(command.asOfDate)

        val threeYearsAnniversarySubscriptions = eventStore.threeYearsAnniversarySubscriptions(date)

        threeYearsAnniversarySubscriptions.forEach {
            val aggregateResult = it.applyThreeYearsAnniversaryDiscount(date)
            eventStore.store(aggregateResult)
        }

        return threeYearsAnniversarySubscriptions.flatMap { it.recentEvents() }
    }
}
