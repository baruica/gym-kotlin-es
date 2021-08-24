package gym.membership.useCases

import gym.membership.domain.NewMemberRegistered
import gym.membership.domain.ThreeYearsAnniversaryThankYouEmailSent
import gym.membership.infrastructure.InMemoryMailer
import gym.membership.infrastructure.InMemoryMemberEventStore
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import java.time.LocalDate
import java.util.*

class Send3YearsAnniversaryThankYouEmailsTest : AnnotationSpec() {

    @Test
    fun handle() {
        val eventStore = InMemoryMemberEventStore()

        val memberSinceJulie = LocalDate.parse("2018-06-05").minusYears(3)
        val newMemberRegisteredJulie = newMemberRegistered("julie@gmail.com", memberSinceJulie)

        val memberSinceBob = LocalDate.parse("2018-06-05").minusYears(2)
        val newMemberRegisteredBob = newMemberRegistered("bob@gmail.com", memberSinceBob)

        val memberSinceLuke = LocalDate.parse("2018-06-05").minusYears(3)
        val newMemberRegisteredLuke = newMemberRegistered("luke@gmail.com", memberSinceLuke)

        eventStore.storeEvents(
            listOf(
                newMemberRegisteredJulie,
                newMemberRegisteredBob,
                newMemberRegisteredLuke
            )
        )

        val mailer = InMemoryMailer()

        val tested = Send3YearsAnniversaryThankYouEmails(eventStore, mailer)

        val events = tested(
            Send3YearsAnniversaryThankYouEmailsCommand("2018-06-05")
        )

        mailer.threeYearsAnniversaryWasSentTo("julie@gmail.com").shouldBeTrue()
        events.shouldContain(
            ThreeYearsAnniversaryThankYouEmailSent(
                newMemberRegisteredJulie.memberId,
                newMemberRegisteredJulie.memberEmailAddress,
                memberSinceJulie.toString()
            )
        )

        mailer.threeYearsAnniversaryWasSentTo("bob@gmail.com").shouldBeFalse()
        events.shouldNotContain(
            ThreeYearsAnniversaryThankYouEmailSent(
                newMemberRegisteredBob.memberId,
                newMemberRegisteredBob.memberEmailAddress,
                memberSinceBob.toString()
            )
        )

        mailer.threeYearsAnniversaryWasSentTo("luke@gmail.com").shouldBeTrue()
        events.shouldContain(
            ThreeYearsAnniversaryThankYouEmailSent(
                newMemberRegisteredLuke.memberId,
                newMemberRegisteredLuke.memberEmailAddress,
                memberSinceLuke.toString()
            )
        )
    }

    private fun newMemberRegistered(
        email: String,
        memberSince: LocalDate
    ): NewMemberRegistered = NewMemberRegistered(
        UUID.randomUUID().toString(),
        email,
        "subscription def",
        memberSince.toString()
    )
}
