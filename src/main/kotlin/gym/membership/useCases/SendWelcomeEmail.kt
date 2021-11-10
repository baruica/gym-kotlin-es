package gym.membership.useCases

import DomainEvent
import gym.membership.domain.Mailer
import gym.membership.domain.MemberEventStore

data class SendWelcomeEmailCommand(val memberId: String)

class SendWelcomeEmail(
    private val eventStore: MemberEventStore,
    private val mailer: Mailer,
) {
    operator fun invoke(event: SendWelcomeEmailCommand): List<DomainEvent> {

        val member = eventStore.get(event.memberId)

        mailer.sendWelcomeEmail(member)

        eventStore.store(member)

        return member.recentEvents()
    }
}
