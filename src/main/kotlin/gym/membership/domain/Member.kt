package gym.membership.domain

import Aggregate
import AggregateHistory
import AggregateId
import DomainEvent
import gym.subscriptions.domain.SubscriptionId
import java.time.LocalDate

class MemberId(memberId: String) : AggregateId(memberId)

class Member private constructor(memberId: String) : Aggregate(memberId) {

    internal lateinit var emailAddress: EmailAddress
    internal lateinit var subscriptionId: String
    internal lateinit var memberSince: LocalDate

    override fun whenEvent(event: DomainEvent) {
        when (event) {
            is NewMemberRegistered -> {
                emailAddress = EmailAddress(event.memberEmailAddress)
                subscriptionId = SubscriptionId(event.subscriptionId).toString()
                memberSince = LocalDate.parse(event.memberSince)
            }
            is WelcomeEmailWasSentToNewMember -> {
                emailAddress = EmailAddress(event.memberEmailAddress)
                memberSince = LocalDate.parse(event.memberSince)
            }
            is ThreeYearsAnniversaryThankYouEmailSent -> {
                memberSince = LocalDate.parse(event.memberSince)
            }
        }
    }

    companion object {
        fun register(
            id: String,
            emailAddress: EmailAddress,
            subscriptionId: String,
            memberSince: LocalDate
        ): Member {
            val member = Member(id)

            member.applyChange(
                NewMemberRegistered(
                    member.id.toString(),
                    emailAddress.toString(),
                    SubscriptionId(subscriptionId).toString(),
                    memberSince.toString()
                )
            )

            return member
        }

        fun restoreFrom(aggregateHistory: AggregateHistory): Member {
            require(aggregateHistory.events.isNotEmpty()) {
                "Cannot restore without any event."
            }

            val member = Member(aggregateHistory.aggregateId)

            aggregateHistory.events.forEach {
                member.whenEvent(it as MemberEvent)
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

    fun isThreeYearsAnniversary(date: LocalDate): Boolean {
        return date.minusYears(3).isEqual(memberSince)
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
