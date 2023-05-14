package gym.membership.useCases

import DomainEvent
import Id
import gym.membership.domain.NewMemberRegistered

class NewMemberRegisteredEventListener(
    private val commandHandler: SendWelcomeEmail.Handler
) {
    fun handle(event: NewMemberRegistered): List<DomainEvent> {

        return commandHandler(
            SendWelcomeEmail(
                Id(event.memberId)
            )
        )
    }
}
