package gym.membership.useCases

import common.DomainEvent
import gym.membership.domain.NewMemberRegistered

class NewMemberRegisteredEventListener(
    private val commandHandler: SendWelcomeEmailToNewMember
) {
    fun handle(event: NewMemberRegistered): List<DomainEvent> {

        return commandHandler.handle(
            SendWelcomeEmailToNewMemberCommand(
                event.memberId
            )
        )
    }
}
