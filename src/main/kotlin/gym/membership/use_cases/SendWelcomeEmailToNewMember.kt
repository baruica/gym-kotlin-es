package gym.membership.use_cases

import common.DomainEvent
import gym.membership.domain.Mailer
import gym.membership.domain.MemberEventStore
import gym.membership.domain.MemberId

class SendWelcomeEmailToNewMember(
    private val eventStore: MemberEventStore,
    private val mailer: Mailer
) {
    fun handle(event: SendWelcomeEmailToNewMemberCommand): List<DomainEvent> {

        val member = eventStore.get(MemberId(event.memberId))

        mailer.sendWelcomeEmail(member)

        eventStore.store(member.changes)

        return member.changes
    }
}
