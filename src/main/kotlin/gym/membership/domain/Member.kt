package gym.membership.domain

import common.AggregateId
import gym.subscriptions.domain.SubscriptionId
import java.time.LocalDate

inline class MemberId(private val id: String) : AggregateId {
    override fun toString(): String = id
}

class Member(
    val memberId: MemberId,
    val email: EmailAddress,
    private val subscriptionId: SubscriptionId,
    private val memberSince: LocalDate
) {
    val recordedEvents: MutableList<MemberEvent> = mutableListOf()

    init {
        recordedEvents.add(
            NewMembership(
                memberId.toString(),
                email.toString(),
                subscriptionId.toString(),
                memberSince.toString()
            )
        )
    }

    fun markWelcomeEmailAsSent() {
        recordedEvents.add(
            WelcomeEmailWasSentToNewMember(
                memberId.toString(),
                email.email,
                subscriptionId.toString()
            )
        )
    }

    fun isThreeYearsAnniversary(asOfDate: LocalDate): Boolean {
        return asOfDate.minusYears(3).isEqual(memberSince)
    }

    fun mark3YearsAnniversaryThankYouEmailAsSent() {
        recordedEvents.add(
            ThreeYearsAnniversaryThankYouEmailSent(
                memberId.toString(),
                memberSince.toString()
            )
        )
    }
}
