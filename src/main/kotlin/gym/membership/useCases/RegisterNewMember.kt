package gym.membership.useCases

import DomainEvent
import gym.membership.domain.EmailAddress
import gym.membership.domain.Member
import gym.membership.domain.MemberEventStore
import gym.subscriptions.domain.SubscriptionId
import java.time.LocalDate

data class RegisterNewMember(
    val subscriptionId: SubscriptionId,
    val subscriptionStartDate: LocalDate,
    val email: EmailAddress,
) {
    class Handler(
        private val eventStore: MemberEventStore
    ) {
        operator fun invoke(command: RegisterNewMember): List<DomainEvent> {

            val knownMember: Member? = eventStore.findByEmailAddress(command.email)

            if (knownMember == null) {
                val aggregateResult = Member.register(
                    eventStore.nextId(),
                    command.email,
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
