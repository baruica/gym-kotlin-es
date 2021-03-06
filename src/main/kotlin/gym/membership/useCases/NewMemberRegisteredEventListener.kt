package gym.membership.useCases

import DomainEvent
import gym.membership.domain.NewMemberRegistered

class NewMemberRegisteredEventListener(
    private val commandHandler: SendWelcomeEmail
) {
    fun handle(event: NewMemberRegistered): List<DomainEvent> {

        return commandHandler.handle(
            SendWelcomeEmailCommand(
                event.memberId
            )
        )
    }
}
