package gym.membership.useCases

import common.AggregateHistory
import common.AggregateId
import common.DomainEvent
import gym.membership.domain.*
import java.time.LocalDate

class InMemoryMemberEventStore : MemberEventStore {

    private val events = mutableMapOf<MemberId, MutableList<MemberEvent>>()

    override fun store(events: List<DomainEvent>) {
        events.forEach {
            this.events.getOrPut(MemberId(it.getAggregateId())) { mutableListOf() }.add(it as MemberEvent)
        }
    }

    override fun get(memberId: MemberId): Member {
        return Member.restoreFrom(getAggregateHistory(memberId))
    }

    override fun getAggregateEvents(aggregateId: AggregateId): MutableList<MemberEvent> =
        this.events.getOrDefault(aggregateId as MemberId, mutableListOf())

    override fun findByEmailAddress(emailAddress: EmailAddress): Member? {
        events.values.forEach { memberEvents ->
            memberEvents.forEach { memberEvent ->
                if (memberEvent.getEmailAddress() == emailAddress.value) {
                    return restoreMember(memberEvent.getAggregateId())
                }
            }
        }

        return null
    }

    override fun threeYearsAnniversaryMembers(date: LocalDate): List<Member> {
        return events.keys
            .map { memberId -> restoreMember(memberId.toString()) }
            .filter { member -> member.isThreeYearsAnniversary(date) }
    }

    private fun restoreMember(aggregateId: String): Member {
        val memberId = MemberId(aggregateId)

        return Member.restoreFrom(
            AggregateHistory(memberId, this.events[memberId]!!.toList())
        )
    }
}
