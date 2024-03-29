package gym.membership.useCases

import gym.membership.domain.EmailAddress
import gym.membership.infrastructure.InMemoryMailer
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeTrue
import java.time.LocalDate

internal class SendSummaryUponSubscriptionTest : StringSpec({

    "handle" {

        val emailAddress = EmailAddress("luke@gmail.com")
        val startDate = LocalDate.parse("2018-06-05")
        val endDate = LocalDate.parse("2018-07-05")
        val price = 250

        val mailer = InMemoryMailer()

        val tested = SendSummaryUponSubscription.Handler(mailer)

        tested(
            SendSummaryUponSubscription(
                emailAddress,
                startDate,
                endDate,
                price
            )
        )

        mailer.subscriptionSummaryEmailWasSentTo(
            emailAddress,
            startDate,
            endDate,
            price
        ).shouldBeTrue()
    }
})
