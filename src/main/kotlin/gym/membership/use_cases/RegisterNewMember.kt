package gym.membership.use_cases

import common.DomainEvent
import gym.membership.domain.EmailAddress
import gym.membership.domain.Member
import gym.membership.domain.MemberEventStore
import gym.subscriptions.domain.SubscriptionId

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
                SubscriptionId(command.subscriptionId),
                command.subscriptionStartDate
            )
            eventStore.store(member)

            return member.occuredEvents()
        }

        return emptyList()
    }
}
