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

            val member = eventStore.get(event.memberId)

            val aggregateResult = mailer.sendWelcomeEmail(member)

            eventStore.store(aggregateResult)

            return aggregateResult.events
        }
    }
}
