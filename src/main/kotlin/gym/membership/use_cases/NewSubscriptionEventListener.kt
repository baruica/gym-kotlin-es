package gym.membership.use_cases

import gym.membership.domain.EmailAddress
import gym.membership.domain.Member
import gym.membership.domain.MemberEvent
import gym.membership.domain.MemberRepository
import gym.subscriptions.domain.NewSubscription
import gym.subscriptions.domain.SubscriptionId
import java.time.LocalDate

class NewSubscriptionEventListener(
    private val memberRepository: MemberRepository
) {
    fun handle(event: NewSubscription): List<MemberEvent> {

        val emailAddress = EmailAddress(event.email)
        val knownMember: Member? = memberRepository.findByEmailAddress(emailAddress)

        if (knownMember == null) {
            val member = Member(
                memberRepository.nextId(),
                emailAddress,
                SubscriptionId(event.subscriptionId),
                LocalDate.parse(event.subscriptionStartDate)
            )
            memberRepository.store(member)

            return member.recordedEvents
        }

        return emptyList()
    }
}
