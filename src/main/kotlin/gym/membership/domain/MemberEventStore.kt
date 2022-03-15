package gym.membership.domain

import EventStore
import java.time.LocalDate

interface MemberEventStore : EventStore<Member, MemberEvent> {

    fun get(memberId: MemberId): Member

    fun findByEmailAddress(emailAddress: EmailAddress): Member?

    fun threeYearsAnniversaryMembers(date: LocalDate): List<Member>
}
