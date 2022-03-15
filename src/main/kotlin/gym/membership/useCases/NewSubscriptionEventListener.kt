package gym.membership.useCases

import DomainEvent
import gym.membership.domain.EmailAddress
import gym.subscriptions.domain.NewSubscription
import gym.subscriptions.domain.SubscriptionId
import java.time.LocalDate

class NewSubscriptionEventListener(
    private val commandHandler: RegisterNewMember.Handler
) {
    fun handle(event: NewSubscription): List<DomainEvent> {

        return commandHandler(
            RegisterNewMember(
                SubscriptionId(event.subscriptionId),
                LocalDate.parse(event.subscriptionStartDate),
                EmailAddress(event.email)
            )
        )
    }
}
