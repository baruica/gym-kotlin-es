package gym.membership.domain

interface Mailer {

    fun sendWelcomeEmail(member: Member)

    fun send3YearsAnniversaryThankYouEmail(member: Member)
}
