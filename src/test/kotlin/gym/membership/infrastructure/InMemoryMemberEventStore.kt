package gym.membership.infrastructure

import InMemoryEventStore
import gym.membership.domain.*
import java.time.LocalDate

class InMemoryMemberEventStore : InMemoryEventStore<Member, MemberEvent>(), MemberEventStore {

    override fun get(memberId: MemberId): Member {
        return Member.restoreFrom(
            getAggregateHistory(memberId.toString())
        )
    }

    override fun findByEmailAddress(emailAddress: EmailAddress): Member? {
        return events.keys
            .map { memberId -> get(MemberId(memberId)) }
            .firstOrNull { member -> member.emailAddress == emailAddress }
    }

    override fun threeYearsAnniversaryMembers(date: LocalDate): List<Member> {
        return events.keys
            .map { memberId -> get(MemberId(memberId)) }
            .filter { member -> member.isThreeYearsAnniversary(date) }
    }
}
