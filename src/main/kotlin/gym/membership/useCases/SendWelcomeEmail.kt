package gym.membership.useCases

import common.DomainEvent
import gym.membership.domain.Mailer
import gym.membership.domain.MemberEventStore
import gym.membership.domain.MemberId

class SendWelcomeEmail(
    private val eventStore: MemberEventStore,
    private val mailer: Mailer,
) {
    fun handle(event: SendWelcomeEmailCommand): List<DomainEvent> {

        val member = eventStore.get(MemberId(event.memberId))

        mailer.sendWelcomeEmail(member)

        eventStore.store(member)

        return member.occuredEvents()
    }
}
