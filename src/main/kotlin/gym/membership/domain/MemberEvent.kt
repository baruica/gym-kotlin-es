package gym.membership.domain

import common.DomainEvent
import java.time.Instant

sealed class MemberEvent : DomainEvent {
    override fun getAggregateId(): String = memberId
    override val created: Instant = Instant.now()

    abstract val memberId: String

    fun getEmailAddress(): String {
        return when (this) {
            is NewMemberRegistered -> memberEmailAddress
            is WelcomeEmailWasSentToNewMember -> memberEmailAddress
            is ThreeYearsAnniversaryThankYouEmailSent -> memberEmailAddress
        }
    }
}

data class NewMemberRegistered(
    override val memberId: String,
    val memberEmailAddress: String,
    val subscriptionId: String,
    val memberSince: String
) : MemberEvent()

data class WelcomeEmailWasSentToNewMember(
    override val memberId: String,
    val memberEmailAddress: String,
    val memberSince: String
) : MemberEvent()

data class ThreeYearsAnniversaryThankYouEmailSent(
    override val memberId: String,
    val memberEmailAddress: String,
    val memberSince: String
) : MemberEvent()
