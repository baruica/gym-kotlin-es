package gym.subscriptions.useCases

import DomainEvent
import gym.subscriptions.domain.SubscriptionEventStore
import java.time.LocalDate

class ApplyThreeYearsAnniversaryDiscount(val asOfDate: String)

class ApplyThreeYearsAnniversaryDiscountHandler(
    private val eventStore: SubscriptionEventStore
) {
    operator fun invoke(command: ApplyThreeYearsAnniversaryDiscount): List<DomainEvent> {

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
