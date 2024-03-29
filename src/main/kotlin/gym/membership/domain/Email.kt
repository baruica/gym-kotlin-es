package gym.membership.domain

import java.time.LocalDate

sealed class Email(
    open val emailAddress: EmailAddress,
    val emailBody: String
) {
    data class Welcome(
        override val emailAddress: EmailAddress
    ) : Email(
        emailAddress,
        "Thank you for subscribing $emailAddress !"
    )

    data class SubscriptionSummary(
        override val emailAddress: EmailAddress,
        val startDate: LocalDate,
        val endDate: LocalDate,
        val price: Int
    ) : Email(
        emailAddress,
        "Thank you for subscribing, this subscription will run from $startDate until $endDate, and will only cost you $price!"
    )

    data class ThreeYearsAnniversary(
        override val emailAddress: EmailAddress
    ) : Email(
        emailAddress,
        "Thank you for your loyalty $emailAddress !"
    )
}
