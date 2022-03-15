package gym.subscriptions.useCases

import DomainEvent
import gym.subscriptions.domain.SubscriptionEventStore
import java.time.LocalDate

class ApplyThreeYearsAnniversaryDiscount(val asOfDate: LocalDate) {
    class Handler(
        private val eventStore: SubscriptionEventStore
    ) {
        operator fun invoke(command: ApplyThreeYearsAnniversaryDiscount): List<DomainEvent> {

            val threeYearsAnniversarySubscriptions = eventStore.threeYearsAnniversarySubscriptions(command.asOfDate)

            val events = mutableListOf<DomainEvent>()

            threeYearsAnniversarySubscriptions.forEach {
                val aggregateResult = it.applyThreeYearsAnniversaryDiscount(command.asOfDate)
                eventStore.store(aggregateResult)
                events.addAll(aggregateResult.events)
            }

            return events
        }
    }
}
