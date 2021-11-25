package gym.membership.useCases

import gym.membership.domain.EmailAddress
import gym.membership.domain.Member
import gym.membership.domain.MemberEvent
import gym.membership.domain.MemberEventStore

data class RegisterNewMemberCommand(
    val subscriptionId: String,
    val subscriptionStartDate: String,
    val email: String,
)

class RegisterNewMember(
    private val eventStore: MemberEventStore
) {
    operator fun invoke(command: RegisterNewMemberCommand): MemberEvent? {

        val emailAddress = EmailAddress(command.email)
        val knownMember: Member? = eventStore.findByEmailAddress(emailAddress)

        if (knownMember == null) {
            val member = Member.register(
                eventStore.nextId(),
                emailAddress,
                command.subscriptionId,
                command.subscriptionStartDate
            )
            eventStore.store(member)

            return member.event as MemberEvent
        }

        return null
    }
}
