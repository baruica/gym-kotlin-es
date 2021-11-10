package gym.membership.useCases

import DomainEvent
import gym.membership.domain.Mailer
import gym.membership.domain.MemberEventStore
import java.time.LocalDate

data class Send3YearsAnniversaryThankYouEmailsCommand(val asOfDate: String)

class Send3YearsAnniversaryThankYouEmails(
    private val eventStore: MemberEventStore,
    private val mailer: Mailer,
) {
    operator fun invoke(command: Send3YearsAnniversaryThankYouEmailsCommand): List<DomainEvent> {

        val threeYearsAnniversaryMembers = eventStore.threeYearsAnniversaryMembers(
            LocalDate.parse(command.asOfDate)
        )

        threeYearsAnniversaryMembers.forEach {
            mailer.send3YearsAnniversaryThankYouEmail(it)
            eventStore.store(it)
        }

        return threeYearsAnniversaryMembers.map {
            it.recentEvents().last()
        }
    }
}
