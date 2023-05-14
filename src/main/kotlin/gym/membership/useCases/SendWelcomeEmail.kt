package gym.membership.useCases

import DomainEvent
import Id
import gym.membership.domain.Mailer
import gym.membership.domain.MemberEventStore

data class SendWelcomeEmail(val memberId: Id<String>) {
    class Handler(
        private val eventStore: MemberEventStore,
        private val mailer: Mailer,
    ) {
        operator fun invoke(event: SendWelcomeEmail): List<DomainEvent> {

            eventStore.get(event.memberId.toString())
                .let { member ->
                    return mailer.sendWelcomeEmail(member)
                        .also { aggregateResult -> eventStore.store(aggregateResult) }
                        .events
                }
        }
    }
}
