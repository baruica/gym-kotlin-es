package gym.membership.domain

import Aggregate
import AggregateHistory
import DomainEvent
import gym.subscriptions.domain.SubscriptionId
import java.time.LocalDate

@JvmInline
value class MemberId(private val id: String) {
    override fun toString(): String = id
}

class Member private constructor(val memberId: MemberId) : Aggregate() {

    internal lateinit var emailAddress: EmailAddress
    internal lateinit var subscriptionId: String
    internal lateinit var memberSince: LocalDate

    override fun getId(): String = memberId.toString()

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
            memberSince: String
        ): Member {
            val member = Member(MemberId(id))

            member.applyChange(
                NewMemberRegistered(
                    member.getId(),
                    emailAddress.toString(),
                    SubscriptionId(subscriptionId).toString(),
                    LocalDate.parse(memberSince).toString()
                )
            )

            return member
        }

        fun restoreFrom(aggregateHistory: AggregateHistory): Member {
            val member = Member(MemberId(aggregateHistory.aggregateId))

            aggregateHistory.events.forEach {
                member.whenEvent(it as MemberEvent)
            }

            return member
        }
    }

    fun markWelcomeEmailAsSent() {
        applyChange(
            WelcomeEmailWasSentToNewMember(
                getId(),
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
                getId(),
                emailAddress.toString(),
                memberSince.toString()
            )
        )
    }
}
