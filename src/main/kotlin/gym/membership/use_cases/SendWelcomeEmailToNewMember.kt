package gym.membership.use_cases

import gym.membership.domain.Mailer
import gym.membership.domain.MemberEvent
import gym.membership.domain.MemberEventStore
import gym.membership.domain.MemberId

class SendWelcomeEmailToNewMember(
    private val eventStore: MemberEventStore,
    private val mailer: Mailer
) {
    fun handle(event: SendWelcomeEmailToNewMemberCommand): List<MemberEvent> {

        val member = eventStore.get(MemberId(event.memberId))

        mailer.sendWelcomeEmail(member)

        eventStore.store(member.recordedEvents)

        return member.recordedEvents
    }
}
