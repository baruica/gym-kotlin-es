package gym.membership.useCases

import DomainEvent
import Id
import gym.membership.domain.EmailAddress
import gym.membership.domain.Member
import gym.membership.domain.MemberEventStore
import java.time.LocalDate

data class RegisterNewMember(
    val subscriptionId: Id<String>,
    val subscriptionStartDate: LocalDate,
    val email: EmailAddress,
) {
    class Handler(
        private val eventStore: MemberEventStore
    ) {
        operator fun invoke(command: RegisterNewMember): List<DomainEvent> {

            eventStore.findByEmailAddress(command.email)
                ?: return Member.register(
                    eventStore.nextId(),
                    command.email,
                    command.subscriptionId,
                    command.subscriptionStartDate
                )
                    .also { eventStore.store(it) }
                    .events

            return listOf()
        }
    }
}
