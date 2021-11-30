package gym.membership.useCases

import DomainEvent
import gym.membership.domain.NewMemberRegistered

class NewMemberRegisteredEventListener(
    private val commandHandler: SendWelcomeEmailHandler
) {
    fun handle(event: NewMemberRegistered): List<DomainEvent> {

        return commandHandler(
            SendWelcomeEmail(
                event.memberId
            )
        )
    }
}
