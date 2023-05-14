package gym.membership.infrastructure

import InMemoryEventStore
import gym.membership.domain.EmailAddress
import gym.membership.domain.Member
import gym.membership.domain.MemberEvent
import gym.membership.domain.MemberEventStore
import java.time.LocalDate

class InMemoryMemberEventStore : InMemoryEventStore<String, Member, MemberEvent>(), MemberEventStore {

    override fun get(memberId: String): Member {
        return Member.restoreFrom(
            getAggregateHistory(memberId)
        )
    }

    override fun findByEmailAddress(emailAddress: EmailAddress): Member? {
        return events.keys
            .map { memberId -> get(memberId) }
            .firstOrNull { member -> member.emailAddress == emailAddress }
    }

    override fun threeYearsAnniversaryMembers(date: LocalDate): List<Member> {
        return events.keys
            .map { memberId -> get(memberId) }
            .filter { member -> member.isThreeYearsAnniversary(date) }
    }
}
