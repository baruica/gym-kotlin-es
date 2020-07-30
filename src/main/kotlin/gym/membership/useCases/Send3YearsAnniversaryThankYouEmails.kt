package gym.membership.useCases

import common.DomainEvent
import gym.membership.domain.Mailer
import gym.membership.domain.MemberEventStore
import java.time.LocalDate

class Send3YearsAnniversaryThankYouEmails(
    private val eventStore: MemberEventStore,
    private val mailer: Mailer
) {
    fun handle(command: Send3YearsAnniversaryThankYouEmailsCommand): List<DomainEvent> {

        val threeYearsAnniversaryMembers = eventStore.threeYearsAnniversaryMembers(
            LocalDate.parse(command.asOfDate)
        )

        threeYearsAnniversaryMembers.forEach {
            mailer.send3YearsAnniversaryThankYouEmail(it)
        }

        return threeYearsAnniversaryMembers.map {
            it.occuredEvents().last()
        }
    }
}
