package gym.membership.domain

import AggregateResult
import java.time.LocalDate

interface Mailer {

    fun sendWelcomeEmail(member: Member): AggregateResult<Member, WelcomeEmailWasSentToNewMember>

    fun sendSubscriptionSummary(
        emailAddress: EmailAddress,
        startDate: LocalDate,
        endDate: LocalDate,
        price: Int
    )

    fun send3YearsAnniversaryThankYouEmail(member: Member): AggregateResult<Member, ThreeYearsAnniversaryThankYouEmailSent>
}
