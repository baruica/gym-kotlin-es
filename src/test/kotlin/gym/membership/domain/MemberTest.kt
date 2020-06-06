package gym.membership.domain

import common.AggregateHistory
import gym.fifthOfJune
import gym.subscriptions.domain.SubscriptionId
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
            SubscriptionId("def"),
            fifthOfJune().minusYears(3).toString()
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
            SubscriptionId("subscription 42"),
            LocalDate.now().toString()
        )
        tested.markWelcomeEmailAsSent()

        val restoredFromEvents = Member.restoreFrom(AggregateHistory(tested.id, tested.occuredEvents()))

        assertEquals(tested, restoredFromEvents)
        assertEquals(emptyList(), tested.occuredEvents())
    }

    @Test
    fun `cannot be restored if no events`() {
        assertFailsWith<IllegalArgumentException> {
            Member.restoreFrom(AggregateHistory(MemberId("memberId 42"), listOf()))
        }
    }
}
