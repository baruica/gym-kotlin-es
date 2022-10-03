package gym.membership.useCases

import DomainEvent
import gym.membership.domain.Mailer
import gym.membership.domain.MemberEventStore
import gym.membership.domain.MemberId

data class SendWelcomeEmail(val memberId: MemberId) {
    class Handler(
        private val eventStore: MemberEventStore,
        private val mailer: Mailer,
    ) {
        operator fun invoke(event: SendWelcomeEmail): List<DomainEvent> {

            eventStore.get(event.memberId)
                .let { member ->
                    return mailer.sendWelcomeEmail(member)
                        .also { aggregateResult -> eventStore.store(aggregateResult) }
                        .events
                }
        }
    }
}
