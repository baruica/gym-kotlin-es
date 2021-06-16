package gym.membership.domain

import AggregateHistory
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldEndWith
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class MemberTest : AnnotationSpec() {

    @Test
    fun `can register`() {
        val email = "luke@gmail.com"
        val subscriptionId = "subscriptionId def"
        val subscriptionStartDate = "2018-06-05"

        val tested = Member.register(
            "member abc",
            EmailAddress(email),
            subscriptionId,
            subscriptionStartDate
        )

        tested.events.shouldEndWith(
            NewMemberRegistered(
                tested.events.last().getAggregateId(),
                email,
                subscriptionId,
                subscriptionStartDate
            )
        )
    }

    @Test
    fun `is 3 years anniversary`() {
        val with3yearsAnniversaryOnTheFifthOfJune = Member.register(
            "member abc",
            EmailAddress("julie@gmail.com"),
            "subscription def",
            LocalDate.parse("2018-06-05").minusYears(3).toString()
        )

        with3yearsAnniversaryOnTheFifthOfJune.isThreeYearsAnniversary(LocalDate.parse("2018-06-04")).shouldBeFalse()
        with3yearsAnniversaryOnTheFifthOfJune.isThreeYearsAnniversary(LocalDate.parse("2018-06-05")).shouldBeTrue()
        with3yearsAnniversaryOnTheFifthOfJune.isThreeYearsAnniversary(LocalDate.parse("2018-07-06")).shouldBeFalse()
    }

    @Test
    fun `can be restored from events`() {
        val tested = Member.register(
            "aggregateId",
            EmailAddress("julie@gmail.com"),
            "subscription 42",
            LocalDate.now().toString()
        )
        tested.markWelcomeEmailAsSent()

        val restoredFromEvents = Member.restoreFrom(AggregateHistory(tested.getId(), tested.recentEvents()))

        restoredFromEvents.emailAddress shouldBe tested.emailAddress
        restoredFromEvents.subscriptionId shouldBe tested.subscriptionId
        restoredFromEvents.memberSince shouldBe tested.memberSince

        tested.recentEvents().shouldBeEmpty()
    }

    @Test
    fun `cannot be restored if no events`() {
        shouldThrow<IllegalArgumentException> {
            Member.restoreFrom(AggregateHistory("memberId 42", listOf()))
        }
    }
}
