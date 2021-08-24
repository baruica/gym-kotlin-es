package gym.membership.useCases

import gym.membership.domain.EmailAddress
import gym.membership.domain.Mailer

class SendSummaryUponSubscription(
    private val mailer: Mailer,
) {
    operator fun invoke(command: SendSummaryUponSubscriptionCommand) {

        mailer.sendSubscriptionSummary(
            EmailAddress(command.email),
            command.startDate,
            command.endDate,
            command.price
        )
    }
}
