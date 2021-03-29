package gym.membership.useCases

import gym.fifthOfJune
import gym.membership.domain.NewMemberRegistered
import gym.membership.domain.ThreeYearsAnniversaryThankYouEmailSent
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class Send3YearsAnniversaryThankYouEmailsTest {

    @Test
    fun handle() {
        val memberEventStore = InMemoryMemberEventStore()

        val memberSinceJulie = fifthOfJune().minusYears(3)
        val newMemberRegisteredJulie = newMemberRegistered("julie@gmail.com", memberSinceJulie)

        val memberSinceBob = fifthOfJune().minusYears(2)
        val newMemberRegisteredBob = newMemberRegistered("bob@gmail.com", memberSinceBob)

        val memberSinceLuke = fifthOfJune().minusYears(3)
        val newMemberRegisteredLuke = newMemberRegistered("luke@gmail.com", memberSinceLuke)

        memberEventStore.store(
            listOf(
                newMemberRegisteredJulie,
                newMemberRegisteredBob,
                newMemberRegisteredLuke
            )
        )

        val mailer = InMemoryMailer()

        val tested = Send3YearsAnniversaryThankYouEmails(memberEventStore, mailer)

        val events = tested.handle(
            Send3YearsAnniversaryThankYouEmailsCommand("2018-06-05")
        )

        assertTrue(mailer.threeYearsAnniversaryWasSentTo("julie@gmail.com"))
        assertTrue(
            events.contains(
                ThreeYearsAnniversaryThankYouEmailSent(
                    newMemberRegisteredJulie.memberId,
                    newMemberRegisteredJulie.memberEmailAddress,
                    memberSinceJulie.toString()
                )
            )
        )

        assertFalse(mailer.threeYearsAnniversaryWasSentTo("bob@gmail.com"))
        assertFalse(
            events.contains(
                ThreeYearsAnniversaryThankYouEmailSent(
                    newMemberRegisteredBob.memberId,
                    newMemberRegisteredBob.memberEmailAddress,
                    memberSinceBob.toString()
                )
            )
        )

        assertTrue(mailer.threeYearsAnniversaryWasSentTo("luke@gmail.com"))
        assertTrue(
            events.contains(
                ThreeYearsAnniversaryThankYouEmailSent(
                    newMemberRegisteredLuke.memberId,
                    newMemberRegisteredLuke.memberEmailAddress,
                    memberSinceLuke.toString()
                )
            )
        )
    }

    private fun newMemberRegistered(email: String, memberSince: LocalDate): NewMemberRegistered = NewMemberRegistered(
        UUID.randomUUID().toString(),
        email,
        "subscription def",
        memberSince.toString()
    )
}
