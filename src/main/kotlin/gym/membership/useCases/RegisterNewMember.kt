package gym.membership.useCases

import DomainEvent
import gym.membership.domain.EmailAddress
import gym.membership.domain.Member
import gym.membership.domain.MemberEventStore
import java.time.LocalDate

class RegisterNewMember(
    private val eventStore: MemberEventStore
) {
    fun handle(command: RegisterNewMemberCommand): List<DomainEvent> {

        val emailAddress = EmailAddress(command.email)
        val knownMember: Member? = eventStore.findByEmailAddress(emailAddress)

        if (knownMember == null) {
            val member = Member.register(
                eventStore.nextId(),
                emailAddress,
                command.subscriptionId,
                LocalDate.parse(command.subscriptionStartDate)
            )
            eventStore.store(member)

            return member.recentEvents()
        }

        return emptyList()
    }
}
