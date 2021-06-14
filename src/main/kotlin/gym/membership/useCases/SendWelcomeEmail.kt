package gym.membership.useCases

import DomainEvent
import gym.membership.domain.Mailer
import gym.membership.domain.MemberEventStore

class SendWelcomeEmail(
    private val eventStore: MemberEventStore,
    private val mailer: Mailer,
) {
    fun handle(event: SendWelcomeEmailCommand): List<DomainEvent> {

        val member = eventStore.get(event.memberId)

        mailer.sendWelcomeEmail(member)

        eventStore.store(member)

        return member.recentEvents()
    }
}
