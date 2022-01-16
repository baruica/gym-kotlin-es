package gym.membership.domain

import Aggregate
import AggregateHistory
import AggregateResult
import DomainEvent
import gym.subscriptions.domain.SubscriptionId
import java.time.LocalDate

@JvmInline
value class MemberId(private val id: String) {
    override fun toString(): String = id
}

class Member private constructor(
    private val id: MemberId
) : Aggregate {

    internal lateinit var emailAddress: EmailAddress
    internal lateinit var subscriptionId: String
    internal lateinit var memberSince: LocalDate

    override fun getId(): String = id.toString()

    private fun whenEvent(event: DomainEvent) {
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
        ): AggregateResult<Member, MemberEvent> {
            val member = Member(MemberId(id))

            val event = NewMemberRegistered(
                member.getId(),
                emailAddress.toString(),
                SubscriptionId(subscriptionId).toString(),
                LocalDate.parse(memberSince).toString()
            )
            member.whenEvent(event)

            return AggregateResult.of(member, event)
        }

        fun restoreFrom(aggregateHistory: AggregateHistory<MemberEvent>): Member {
            val member = Member(MemberId(aggregateHistory.aggregateId))

            aggregateHistory.events.forEach {
                member.whenEvent(it)
            }

            return member
        }
    }

    fun markWelcomeEmailAsSent(): AggregateResult<Member, WelcomeEmailWasSentToNewMember> {
        val event = WelcomeEmailWasSentToNewMember(
            getId(),
            emailAddress.value,
            memberSince.toString()
        )
        whenEvent(event)

        return AggregateResult.of(this, event)
    }

    fun isThreeYearsAnniversary(date: LocalDate): Boolean {
        return date.minusYears(3).isEqual(memberSince)
    }

    fun mark3YearsAnniversaryThankYouEmailAsSent(): AggregateResult<Member, ThreeYearsAnniversaryThankYouEmailSent> {
        val event = ThreeYearsAnniversaryThankYouEmailSent(
            getId(),
            emailAddress.toString(),
            memberSince.toString()
        )
        whenEvent(event)

        return AggregateResult.of(this, event)
    }
}
