package gym.membership.useCases

import DomainEvent
import gym.membership.domain.EmailAddress
import gym.membership.domain.Member
import gym.membership.domain.MemberEventStore

data class RegisterNewMember(
    val subscriptionId: String,
    val subscriptionStartDate: String,
    val email: String,
) {
    class Handler(
        private val eventStore: MemberEventStore
    ) {
        operator fun invoke(command: RegisterNewMember): List<DomainEvent> {

            val emailAddress = EmailAddress(command.email)
            val knownMember: Member? = eventStore.findByEmailAddress(emailAddress)

            if (knownMember == null) {
                val aggregateResult = Member.register(
                    eventStore.nextId(),
                    emailAddress,
                    command.subscriptionId,
                    command.subscriptionStartDate
                )
                eventStore.store(aggregateResult)

                return aggregateResult.events
            }

            return listOf()
        }
    }
}
