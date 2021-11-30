package gym.membership.useCases

import DomainEvent
import gym.membership.domain.Mailer
import gym.membership.domain.MemberEventStore
import java.time.LocalDate

data class Send3YearsAnniversaryThankYouEmails(val asOfDate: String)

class Send3YearsAnniversaryThankYouEmailsHandler(
    private val eventStore: MemberEventStore,
    private val mailer: Mailer,
) {
    operator fun invoke(command: Send3YearsAnniversaryThankYouEmails): List<DomainEvent> {

        val threeYearsAnniversaryMembers = eventStore.threeYearsAnniversaryMembers(
            LocalDate.parse(command.asOfDate)
        )

        val events = mutableListOf<DomainEvent>()

        threeYearsAnniversaryMembers.forEach {
            val aggregateResult = mailer.send3YearsAnniversaryThankYouEmail(it)
            eventStore.store(aggregateResult)
            events.addAll(aggregateResult.events)
        }

        return events
    }
}
