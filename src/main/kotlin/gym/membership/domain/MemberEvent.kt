package gym.membership.domain

import DomainEvent

sealed class MemberEvent : DomainEvent {
    override fun getAggregateId(): String = memberId

    abstract val memberId: String
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
