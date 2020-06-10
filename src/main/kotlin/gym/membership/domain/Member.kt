package gym.membership.domain

import common.Aggregate
import common.AggregateHistory
import common.AggregateId
import common.DomainEvent
import gym.subscriptions.domain.SubscriptionId
import java.time.LocalDate

inline class MemberId(private val id: String) : AggregateId {
    override fun toString(): String = id
}

class Member private constructor(memberId: MemberId) : Aggregate<MemberId>(memberId) {

    lateinit var emailAddress: EmailAddress
    private lateinit var subscriptionId: String
    private lateinit var memberSince: LocalDate

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
            val member = Member(MemberId(id))

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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Member

        if (id != other.id) return false
        if (emailAddress != other.emailAddress) return false
        if (subscriptionId != other.subscriptionId) return false
        if (memberSince != other.memberSince) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + emailAddress.hashCode()
        result = 31 * result + subscriptionId.hashCode()
        result = 31 * result + memberSince.hashCode()
        return result
    }
}
