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

        val events = mutableListOf<DomainEvent>()

        threeYearsAnniversarySubscriptions.forEach {
            val aggregateResult = it.applyThreeYearsAnniversaryDiscount(date)
            eventStore.store(aggregateResult)
            events.addAll(aggregateResult.events)
        }

        return events
    }
}
