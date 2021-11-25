package gym.membership.domain

import AggregateResult

interface Mailer {

    fun sendWelcomeEmail(member: Member): AggregateResult<Member, WelcomeEmailWasSentToNewMember>

    fun sendSubscriptionSummary(
        emailAddress: EmailAddress,
        startDate: String,
        endDate: String,
        price: Int
    )

    fun send3YearsAnniversaryThankYouEmail(member: Member): AggregateResult<Member, ThreeYearsAnniversaryThankYouEmailSent>
}
