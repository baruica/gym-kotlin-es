package gym.membership.use_cases

import gym.membership.domain.Mailer
import gym.membership.domain.MemberEvent
import gym.membership.domain.MemberEventStore
import java.time.LocalDate

class Send3YearsAnniversaryThankYouEmails(
    private val eventStore: MemberEventStore,
    private val mailer: Mailer
) {
    fun handle(command: Send3YearsAnniversaryThankYouEmailsCommand): List<MemberEvent> {

        val threeYearsAnniversaryMembers = eventStore.threeYearsAnniversaryMembers(
            LocalDate.parse(command.asOfDate)
        )

        threeYearsAnniversaryMembers.map {
            mailer.send3YearsAnniversaryThankYouEmail(it)
        }

        return threeYearsAnniversaryMembers.map {
            it.recordedEvents.last()
        }
    }
}
