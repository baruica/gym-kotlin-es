package gym.membership.useCases

import gym.membership.domain.MemberId
import gym.membership.domain.NewMemberRegistered
import gym.membership.domain.WelcomeEmailWasSentToNewMember
import gym.subscriptions.domain.SubscriptionId
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SendWelcomeEmailTest {

    @Test
    fun handle() {

        val memberId = MemberId("member abc")
        val emailAddress = "bob@gmail.com"
        val subscriptionId = SubscriptionId("subscription def")
        val memberSince = LocalDate.now()

        val eventStore = InMemoryMemberEventStore()
        eventStore.store(
            listOf(
                NewMemberRegistered(
                    memberId.toString(),
                    emailAddress,
                    subscriptionId.toString(),
                    memberSince.toString()
                )
            )
        )

        val mailer = InMemoryMailer()

        val tested = SendWelcomeEmail(eventStore, mailer)

        val events = tested.handle(
            SendWelcomeEmailCommand(memberId.toString())
        )

        assertEquals(
            events.last(),
            WelcomeEmailWasSentToNewMember(
                memberId.toString(),
                emailAddress,
                memberSince.toString()
            )
        )
        assertTrue(mailer.welcomeEmailWasSentTo("bob@gmail.com"))
    }
}
