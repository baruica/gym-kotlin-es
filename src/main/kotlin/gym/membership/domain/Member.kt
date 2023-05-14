package gym.membership.domain

import AggregateHistory
import AggregateResult
import DomainEvent
import Id
import Identifiable
import java.time.LocalDate

class Member private constructor(
    override val id: Id<String>
) : Identifiable<String> {

    internal lateinit var emailAddress: EmailAddress
    internal lateinit var subscriptionId: String
    internal lateinit var memberSince: LocalDate

    private fun whenEvent(event: DomainEvent) {
        when (event) {
            is NewMemberRegistered -> {
                emailAddress = EmailAddress(event.memberEmailAddress)
                subscriptionId = Id(event.subscriptionId).toString()
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
            subscriptionId: Id<String>,
            memberSince: LocalDate
        ): AggregateResult<String, Member, MemberEvent> {
            val member = Member(Id(id))

            val event = NewMemberRegistered(
                member.id.toString(),
                emailAddress.toString(),
                subscriptionId.toString(),
                memberSince.toString()
            )
            member.whenEvent(event)

            return AggregateResult(member, event)
        }

        fun restoreFrom(aggregateHistory: AggregateHistory<String, MemberEvent>): Member {
            val member = Member(aggregateHistory.aggregateId)

            aggregateHistory.events.forEach {
                member.whenEvent(it)
            }

            return member
        }
    }

    fun markWelcomeEmailAsSent(): AggregateResult<String, Member, WelcomeEmailWasSentToNewMember> {
        val event = WelcomeEmailWasSentToNewMember(
            id.toString(),
            emailAddress.value,
            memberSince.toString()
        )
        whenEvent(event)

        return AggregateResult(this, event)
    }

    fun isThreeYearsAnniversary(date: LocalDate): Boolean {
        return date.minusYears(3).isEqual(memberSince)
    }

    fun mark3YearsAnniversaryThankYouEmailAsSent(): AggregateResult<String, Member, ThreeYearsAnniversaryThankYouEmailSent> {
        val event = ThreeYearsAnniversaryThankYouEmailSent(
            id.toString(),
            emailAddress.toString(),
            memberSince.toString()
        )
        whenEvent(event)

        return AggregateResult(this, event)
    }
}
