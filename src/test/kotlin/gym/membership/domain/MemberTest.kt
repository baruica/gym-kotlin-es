package gym.membership.domain

import common.AggregateHistory
import gym.fifthOfJune
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MemberTest {

    @Test
    fun `is 3 years anniversary`() {
        val memberWith3yearsAnniversaryOnTheFifthOfJune = Member.register(
            "member abc",
            EmailAddress("julie@gmail.com"),
            "subscription def",
            fifthOfJune().minusYears(3)
        )

        assertFalse(memberWith3yearsAnniversaryOnTheFifthOfJune.isThreeYearsAnniversary(LocalDate.parse("2018-06-04")))
        assertTrue(memberWith3yearsAnniversaryOnTheFifthOfJune.isThreeYearsAnniversary(fifthOfJune()))
        assertFalse(memberWith3yearsAnniversaryOnTheFifthOfJune.isThreeYearsAnniversary(LocalDate.parse("2018-07-06")))
    }

    @Test
    fun `can be restored from events`() {
        val tested = Member.register(
            "aggregateId",
            EmailAddress("julie@gmail.com"),
            "subscription 42",
            LocalDate.now()
        )
        tested.markWelcomeEmailAsSent()

        val restoredFromEvents = Member.restoreFrom(AggregateHistory(tested.id, tested.occuredEvents()))

        assertEquals(tested.emailAddress, restoredFromEvents.emailAddress)
        assertEquals(tested.subscriptionId, restoredFromEvents.subscriptionId)
        assertEquals(tested.memberSince, restoredFromEvents.memberSince)

        assertEquals(emptyList(), tested.occuredEvents())
    }

    @Test
    fun `cannot be restored if no events`() {
        assertFailsWith<IllegalArgumentException> {
            Member.restoreFrom(AggregateHistory(MemberId("memberId 42"), listOf()))
        }
    }
}
