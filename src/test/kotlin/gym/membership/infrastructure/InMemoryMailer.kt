package gym.membership.infrastructure

import gym.membership.domain.Email
import gym.membership.domain.Email.*
import gym.membership.domain.EmailAddress
import gym.membership.domain.Mailer
import gym.membership.domain.Member
import java.util.*

class InMemoryMailer(
    private val sentEmails: MutableMap<String, Email> = mutableMapOf()
) : Mailer {

    override fun sendWelcomeEmail(member: Member) {
        sentEmails[UUID.randomUUID().toString()] = Welcome(member.emailAddress)
        member.markWelcomeEmailAsSent()
    }

    override fun sendSubscriptionSummary(
        emailAddress: EmailAddress,
        startDate: String,
        endDate: String,
        price: Int
    ) {
        sentEmails[UUID.randomUUID().toString()] = SubscriptionSummary(emailAddress, startDate, endDate, price)
    }

    override fun send3YearsAnniversaryThankYouEmail(member: Member) {
        sentEmails[UUID.randomUUID().toString()] = ThreeYearsAnniversary(member.emailAddress)
        member.mark3YearsAnniversaryThankYouEmailAsSent()
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
