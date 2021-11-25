package gym.membership.infrastructure

import AggregateResult
import gym.membership.domain.*
import gym.membership.domain.Email.*
import java.util.*

class InMemoryMailer(
    private val sentEmails: MutableMap<String, Email> = mutableMapOf()
) : Mailer {

    override fun sendWelcomeEmail(member: Member): AggregateResult<Member, WelcomeEmailWasSentToNewMember> {
        sentEmails[UUID.randomUUID().toString()] = Welcome(member.emailAddress)

        return member.markWelcomeEmailAsSent()
    }

    override fun sendSubscriptionSummary(
        emailAddress: EmailAddress,
        startDate: String,
        endDate: String,
        price: Int
    ) {
        sentEmails[UUID.randomUUID().toString()] = SubscriptionSummary(emailAddress, startDate, endDate, price)
    }

    override fun send3YearsAnniversaryThankYouEmail(member: Member): AggregateResult<Member, ThreeYearsAnniversaryThankYouEmailSent> {
        sentEmails[UUID.randomUUID().toString()] = ThreeYearsAnniversary(member.emailAddress)

        return member.mark3YearsAnniversaryThankYouEmailAsSent()
    }

    internal fun welcomeEmailWasSentTo(emailAddress: String): Boolean {
        return sentEmails.containsValue(
            Welcome(EmailAddress(emailAddress))
        )
    }

    internal fun subscriptionSummaryEmailWasSentTo(
        emailAddress: EmailAddress,
        startDate: String,
        endDate: String,
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
