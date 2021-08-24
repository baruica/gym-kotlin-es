package gym.membership.useCases

import DomainEvent
import gym.subscriptions.domain.NewSubscription

class NewSubscriptionEventListener(
    private val commandHandler: RegisterNewMember
) {
    fun handle(event: NewSubscription): List<DomainEvent> {

        return commandHandler(
            RegisterNewMemberCommand(
                event.subscriptionId,
                event.subscriptionStartDate,
                event.email
            )
        )
    }
}
