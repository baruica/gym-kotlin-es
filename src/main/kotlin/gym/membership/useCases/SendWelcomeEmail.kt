package gym.membership.useCases

import DomainEvent
import gym.membership.domain.Mailer
import gym.membership.domain.MemberEventStore

data class SendWelcomeEmail(val memberId: String)

class SendWelcomeEmailHandler(
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
