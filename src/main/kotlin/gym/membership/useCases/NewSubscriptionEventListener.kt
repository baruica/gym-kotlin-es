package gym.membership.useCases

import DomainEvent
import Id
import gym.membership.domain.EmailAddress
import gym.subscriptions.domain.NewSubscription
import java.time.LocalDate

class NewSubscriptionEventListener(
    private val commandHandler: RegisterNewMember.Handler
) {
    fun handle(event: NewSubscription): List<DomainEvent> {

        return commandHandler(
            RegisterNewMember(
                Id(event.subscriptionId),
                LocalDate.parse(event.subscriptionStartDate),
                EmailAddress(event.email)
            )
        )
    }
}
