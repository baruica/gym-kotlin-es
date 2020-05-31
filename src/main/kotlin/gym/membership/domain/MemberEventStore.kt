package gym.membership.domain

import common.EventStore
import java.time.LocalDate

interface MemberEventStore : EventStore {

    fun get(memberId: MemberId): Member

    fun findByEmailAddress(emailAddress: EmailAddress): Member?

    fun threeYearsAnniversaryMembers(date: LocalDate): List<Member>
}
