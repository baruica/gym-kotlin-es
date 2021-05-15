package gym.membership.domain

import common.AggregateHistory
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class MemberTest : AnnotationSpec() {

    @Test
    fun `is 3 years anniversary`() {
        val memberWith3yearsAnniversaryOnTheFifthOfJune = Member.register(
            "member abc",
            EmailAddress("julie@gmail.com"),
            "subscription def",
            LocalDate.parse("2018-06-05").minusYears(3)
        )

        memberWith3yearsAnniversaryOnTheFifthOfJune.isThreeYearsAnniversary(LocalDate.parse("2018-06-04")).shouldBeFalse()
        memberWith3yearsAnniversaryOnTheFifthOfJune.isThreeYearsAnniversary(LocalDate.parse("2018-06-05")).shouldBeTrue()
        memberWith3yearsAnniversaryOnTheFifthOfJune.isThreeYearsAnniversary(LocalDate.parse("2018-07-06")).shouldBeFalse()
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

        restoredFromEvents.emailAddress shouldBe tested.emailAddress
        restoredFromEvents.subscriptionId shouldBe tested.subscriptionId
        restoredFromEvents.memberSince shouldBe tested.memberSince

        tested.occuredEvents().shouldBeEmpty()
    }

    @Test
    fun `cannot be restored if no events`() {
        shouldThrow<IllegalArgumentException> {
            Member.restoreFrom(AggregateHistory(MemberId("memberId 42"), listOf()))
        }
    }
}
