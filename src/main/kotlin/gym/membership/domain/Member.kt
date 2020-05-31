package gym.membership.domain

import common.AggregateHistory
import common.AggregateId
import gym.subscriptions.domain.SubscriptionId
import java.time.LocalDate

inline class MemberId(private val id: String) : AggregateId {
    override fun toString(): String = id
}

class Member private constructor(val id: MemberId) {

    lateinit var emailAddress: EmailAddress
    private lateinit var subscriptionId: String
    private lateinit var memberSince: LocalDate

    val recordedEvents = mutableListOf<MemberEvent>()

    private fun applyChange(event: MemberEvent) {
        when (event) {
            is NewMemberRegistered -> apply(event)
            is WelcomeEmailWasSentToNewMember -> apply(event)
            is ThreeYearsAnniversaryThankYouEmailSent -> apply(event)
        }

        recordedEvents.add(event)
    }

    private fun apply(event: NewMemberRegistered) {
        emailAddress = EmailAddress(event.memberEmailAddress)
        subscriptionId = SubscriptionId(event.subscriptionId).toString()
        memberSince = LocalDate.parse(event.memberSince)
    }

    private fun apply(event: WelcomeEmailWasSentToNewMember) {
        emailAddress = EmailAddress(event.memberEmailAddress)
        memberSince = LocalDate.parse(event.memberSince)
    }

    private fun apply(event: ThreeYearsAnniversaryThankYouEmailSent) {
        memberSince = LocalDate.parse(event.memberSince)
    }

    companion object {
        fun register(
            id: MemberId,
            emailAddress: EmailAddress,
            subscriptionId: SubscriptionId,
            memberSince: LocalDate
        ): Member {
            val member = Member(id)

            member.applyChange(
                NewMemberRegistered(
                    member.id.toString(),
                    emailAddress.toString(),
                    subscriptionId.toString(),
                    memberSince.toString()
                )
            )

            return member
        }

        fun restoreFrom(aggregateHistory: AggregateHistory): Member {
            require(aggregateHistory.events.isNotEmpty()) {
                "Cannot restore without any event."
            }

            val member = Member(
                MemberId(aggregateHistory.aggregateId.toString())
            )

            aggregateHistory.events.forEach {
                member.applyChange(it as MemberEvent)
            }

            return member
        }
    }

    fun markWelcomeEmailAsSent() {
        applyChange(
            WelcomeEmailWasSentToNewMember(
                id.toString(),
                emailAddress.value,
                memberSince.toString()
            )
        )
    }

    fun isThreeYearsAnniversary(asOfDate: LocalDate): Boolean {
        return asOfDate.minusYears(3).isEqual(memberSince)
    }

    fun mark3YearsAnniversaryThankYouEmailAsSent() {
        applyChange(
            ThreeYearsAnniversaryThankYouEmailSent(
                id.toString(),
                emailAddress.toString(),
                memberSince.toString()
            )
        )
    }
}
