package gym.membership.domain

import EventStore
import java.time.LocalDate

interface MemberEventStore : EventStore<String, Member, MemberEvent> {

    fun get(memberId: String): Member

    fun findByEmailAddress(emailAddress: EmailAddress): Member?

    fun threeYearsAnniversaryMembers(date: LocalDate): List<Member>
}
