package gym.membership.useCases

import gym.membership.domain.EmailAddress
import gym.membership.domain.Mailer
import java.time.LocalDate

data class SendSummaryUponSubscription(
    val email: EmailAddress,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val price: Int
) {
    class Handler(
        private val mailer: Mailer,
    ) {
        operator fun invoke(command: SendSummaryUponSubscription) {
            mailer.sendSubscriptionSummary(
                command.email,
                command.startDate,
                command.endDate,
                command.price
            )
        }
    }
}
