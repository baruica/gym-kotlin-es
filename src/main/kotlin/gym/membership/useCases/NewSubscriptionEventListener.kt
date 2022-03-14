package gym.membership.useCases

import DomainEvent
import gym.subscriptions.domain.NewSubscription

class NewSubscriptionEventListener(
    private val commandHandler: RegisterNewMember.Handler
) {
    fun handle(event: NewSubscription): List<DomainEvent> {

        return commandHandler(
            RegisterNewMember(
                event.subscriptionId,
                event.subscriptionStartDate,
                event.email
            )
        )
    }
}
