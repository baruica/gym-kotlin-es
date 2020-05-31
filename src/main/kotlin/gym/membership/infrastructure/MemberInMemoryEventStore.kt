package gym.membership.infrastructure

import common.AggregateHistory
import common.AggregateId
import common.DomainEvent
import gym.membership.domain.*
import java.time.LocalDate
import java.util.*

class MemberInMemoryEventStore : MemberEventStore {

    private val events = mutableMapOf<MemberId, MutableList<MemberEvent>>()

    override fun nextId(): String {
        return UUID.randomUUID().toString()
    }

    override fun store(events: List<DomainEvent>) {
        events.forEach {
            this.events.getOrPut(MemberId(it.aggregateId())) { mutableListOf() }.add(it as MemberEvent)
        }
    }

    override fun getAggregateHistoryFor(aggregateId: AggregateId): AggregateHistory {
        return AggregateHistory(
            aggregateId,
            getAggregateEvents(aggregateId)
        )
    }

    override fun get(memberId: MemberId): Member {
        return Member.restoreFrom(getAggregateHistoryFor(memberId))
    }

    override fun findByEmailAddress(emailAddress: EmailAddress): Member? {
        events.values.forEach { memberEvents ->
            memberEvents.forEach { memberEvent ->
                if (memberEvent.getEmailAddress() == emailAddress.value) {
                    return restoreMember(memberEvent.aggregateId())
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

    private fun getAggregateEvents(id: AggregateId): MutableList<MemberEvent> =
        this.events.getOrDefault(id as MemberId, mutableListOf())
}
