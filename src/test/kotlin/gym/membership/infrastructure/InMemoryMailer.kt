package gym.membership.infrastructure

import AggregateResult
import com.github.guepardoapps.kulid.ULID
import gym.membership.domain.*
import gym.membership.domain.Email.*
import java.time.LocalDate

class InMemoryMailer(
    private val sentEmails: MutableMap<String, Email> = mutableMapOf()
) : Mailer {

    override fun sendWelcomeEmail(member: Member): AggregateResult<String, Member, WelcomeEmailWasSentToNewMember> {
        sentEmails[ULID.random()] = Welcome(member.emailAddress)

        return member.markWelcomeEmailAsSent()
    }

    override fun sendSubscriptionSummary(
        emailAddress: EmailAddress,
        startDate: LocalDate,
        endDate: LocalDate,
        price: Int
    ) {
        sentEmails[ULID.random()] = SubscriptionSummary(emailAddress, startDate, endDate, price)
    }

    override fun send3YearsAnniversaryThankYouEmail(member: Member): AggregateResult<String, Member, ThreeYearsAnniversaryThankYouEmailSent> {
        sentEmails[ULID.random()] = ThreeYearsAnniversary(member.emailAddress)

        return member.mark3YearsAnniversaryThankYouEmailAsSent()
    }

    internal fun welcomeEmailWasSentTo(emailAddress: String): Boolean {
        return sentEmails.containsValue(
            Welcome(EmailAddress(emailAddress))
        )
    }

    internal fun subscriptionSummaryEmailWasSentTo(
        emailAddress: EmailAddress,
        startDate: LocalDate,
        endDate: LocalDate,
        price: Int
    ): Boolean {
        return sentEmails.containsValue(
            SubscriptionSummary(
                emailAddress,
                startDate,
                endDate,
                price
            )
        )
    }

    internal fun threeYearsAnniversaryWasSentTo(emailAddress: String): Boolean {
        return sentEmails.containsValue(
            ThreeYearsAnniversary(EmailAddress(emailAddress))
        )
    }
}
